//
//  TokenManager.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/3/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "TokenManager.h"
#import "Constants.h"
#import "Base64.h"
#import "NSString+URLEncode.h"
#import "TokenDevice.h"

// Constants for ClientData.typ
NSString* const REQUEST_TYPE_REGISTER = @"navigator.id.finishEnrollment";
NSString* const REQUEST_TYPE_AUTHENTICATE = @"navigator.id.getAssertion";
//for decline
NSString* const REGISTER_CANCEL_TYPE = @"navigator.id.cancelEnrollment";
NSString* const AUTHENTICATE_CANCEL_TYPE = @"navigator.id.cancelAssertion";

// Constants for building ClientData.challenge
NSString* const JSON_PROPERTY_REGISTER_REQUEST  = @"registerRequests";
NSString* const JSON_PROPERTY_AUTENTICATION_REQUEST  = @"authenticateRequests";
NSString* const JSON_PROPERTY_REQUEST_TYPE  = @"typ";
NSString* const JSON_PROPERTY_SERVER_CHALLENGE = @"challenge";
NSString* const JSON_PROPERTY_SERVER_ORIGIN = @"origin";
NSString* const JSON_PROPERTY_VERSION = @"version";
NSString* const JSON_PROPERTY_APP_ID = @"appId";
NSString* const JSON_PROPERTY_KEY_HANDLE = @"keyHandle";

NSString* const SUPPORTED_U2F_VERSION = @"U2F_V2";

Byte USER_PRESENT_FLAG = 0x01;
Byte USER_PRESENCE_SIGN = 0x03;
Byte CHECK_ONLY = 0x07;

@implementation TokenManager

-(id)init{
    
    codec = [[RawMessageCodec alloc] init];
    u2FKey = [[U2FKeyImpl alloc] init];

    return self;
}

-(TokenResponse*)enroll:(NSDictionary*)parameters baseUrl:(NSString*)baseUrl isDecline:(BOOL)isDecline{
    
    NSDictionary* registerRequests = [parameters objectForKey:JSON_PROPERTY_REGISTER_REQUEST];
    
    NSString* version = [registerRequests valueForKey:JSON_PROPERTY_VERSION];
    NSString* appParam = [registerRequests valueForKey:JSON_PROPERTY_APP_ID];
    NSString* challenge = [registerRequests valueForKey:JSON_PROPERTY_SERVER_CHALLENGE];
    
    if ([appParam isKindOfClass:[NSArray class]]){
        appParam = [((NSArray*)appParam) objectAtIndex:0];
    }
    if ([version isKindOfClass:[NSArray class]]){
        version = [((NSArray*)version) objectAtIndex:0];
    }
    if ([challenge isKindOfClass:[NSArray class]]){
        challenge = [((NSArray*)challenge) objectAtIndex:0];
    }
    
    if (![version isEqualToString:SUPPORTED_U2F_VERSION]){
        [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_UNSUPPORTED_VERSION object:nil];
    }
    
    //Need create EnrollmentResponse
    EnrollmentResponse* enrollmentResponse = [u2FKey registerRequest:[[EnrollmentRequest alloc] initWithVersion:version challenge:challenge application:appParam issuer:baseUrl] isDecline:isDecline];
    NSData* result = [codec encodeRegisterResponse:enrollmentResponse];
    NSString* resultForService = [result base64EncodedString];
    
    NSMutableDictionary* clientData = [[NSMutableDictionary alloc] init];
    if (isDecline){
        [clientData setObject:REGISTER_CANCEL_TYPE forKey:JSON_PROPERTY_REQUEST_TYPE];
    } else {
        [clientData setObject:REQUEST_TYPE_REGISTER forKey:JSON_PROPERTY_REQUEST_TYPE];
    }
    [clientData setObject:challenge forKey:JSON_PROPERTY_SERVER_CHALLENGE];
    [clientData setObject:baseUrl forKey:JSON_PROPERTY_SERVER_ORIGIN];

    NSData *clientDataString = [NSJSONSerialization dataWithJSONObject:clientData options:NSJSONWritingPrettyPrinted error:nil];
    
    NSData* tokenDeviceData = [[TokenDevice sharedInstance] getTokenDeviceJSON];
    
    NSMutableDictionary* response = [[NSMutableDictionary alloc] init];
    [response setObject:resultForService forKey:@"registrationData"];
    [response setObject:[clientDataString base64EncodedStringWithOptions:0] forKey:@"clientData"];
    [response setObject:[tokenDeviceData base64EncodedStringWithOptions:0] forKey:@"deviceData"];
    
    NSError * err;
    NSData * jsonData = [NSJSONSerialization dataWithJSONObject:response options:0 error:&err];
    NSString * responseJSONString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    
    responseJSONString = [responseJSONString URLEncode];
    
    TokenResponse* tokenResponse = [[TokenResponse alloc] init];
    [tokenResponse setResponse:responseJSONString];
    [tokenResponse setChallenge:challenge];
    [tokenResponse setKeyHandle:[[enrollmentResponse keyHandle] base64EncodedStringWithOptions:0]];
    
    return tokenResponse;
}

-(TokenResponse*)sign:(NSDictionary*)parameters baseUrl:(NSString*)baseUrl isDecline:(BOOL)isDecline{
    NSArray* autenticateRequests = [parameters objectForKey:JSON_PROPERTY_AUTENTICATION_REQUEST];
    AuthenticateResponse* authenticateResponse = [[AuthenticateResponse alloc] init];
    NSString* authenticatedChallenge;
    NSDictionary* authRequest = [[NSDictionary alloc] init];
    for (NSDictionary* autenticateRequest in autenticateRequests){
        authRequest = autenticateRequest;
        
        NSString* version = [authRequest valueForKey:JSON_PROPERTY_VERSION];
        NSString* appParam = [authRequest valueForKey:JSON_PROPERTY_APP_ID];
        NSString* challenge = [authRequest valueForKey:JSON_PROPERTY_SERVER_CHALLENGE];
        NSData* keyHandle = [[authRequest valueForKey:JSON_PROPERTY_KEY_HANDLE] base64DecodedData];
        
        if (![version isEqualToString:SUPPORTED_U2F_VERSION]){
            [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_UNSUPPORTED_VERSION object:nil];
        }
        NSData* controlData = [[NSData alloc] initWithBytes:&USER_PRESENCE_SIGN length:1];
        authenticateResponse = [u2FKey autenticate:[[AuthenticateRequest alloc] initWithVersion:version control:controlData challenge:challenge application:appParam keyHandle:keyHandle]];
        
        if (authenticateResponse != nil){
            authenticatedChallenge = challenge;
            break;
        }
    }
    if (authenticateResponse == nil){
        return nil;
    }
    
    NSMutableDictionary* clientMutableData = [[NSMutableDictionary alloc] init];
    if (isDecline){
        [clientMutableData setObject:AUTHENTICATE_CANCEL_TYPE forKey:JSON_PROPERTY_REQUEST_TYPE];
    } else {
        [clientMutableData setObject:REQUEST_TYPE_AUTHENTICATE forKey:JSON_PROPERTY_REQUEST_TYPE];
    }
    [clientMutableData setObject:[authRequest valueForKey:JSON_PROPERTY_SERVER_CHALLENGE] forKey:JSON_PROPERTY_SERVER_CHALLENGE];
    [clientMutableData setObject:baseUrl forKey:JSON_PROPERTY_SERVER_ORIGIN];
    
    NSString* keyHandle = [authRequest valueForKey:JSON_PROPERTY_KEY_HANDLE];
    NSData *clientData = [NSJSONSerialization dataWithJSONObject:clientMutableData options:NSJSONWritingPrettyPrinted error:nil];
    NSString* clientDataString = [clientData base64EncodedStringWithOptions:0];
    
    NSData* resp = [codec encodeAuthenticateResponse:authenticateResponse];
    
    NSMutableDictionary* response = [[NSMutableDictionary alloc] init];
    [response setObject:[resp base64EncodedString] forKey:@"signatureData"];
    [response setObject:clientDataString forKey:@"clientData"];
    [response setObject:keyHandle forKey:@"keyHandle"];
    
    NSError * err;
    NSData * jsonData = [NSJSONSerialization dataWithJSONObject:response options:0 error:&err];
    NSString * responseJSONString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    
    responseJSONString = [responseJSONString URLEncode];
    
    TokenResponse* tokenResponse = [[TokenResponse alloc] init];
    [tokenResponse setResponse:responseJSONString];
    [tokenResponse setChallenge:authenticatedChallenge];
    [tokenResponse setKeyHandle:keyHandle];
    
    return tokenResponse;
}

@end
