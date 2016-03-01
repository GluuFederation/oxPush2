//
//  OXPushManager.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/9/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "OXPushManager.h"
#import "OxPush2Request.h"
#import "ApiServiceManager.h"
#import "DataStoreManager.h"
#import "TokenEntity.h"
#import "U2fMetaData.h"
#import "TokenManager.h"
#import "Constants.h"
#import "Base64.h"
#import "NSString+URLEncode.h"
#import "TokenDevice.h"
#import "UserLoginInfo.h"

@implementation OXPushManager

-(void)onOxPushApproveRequest:(NSDictionary*)parameters{
    NSString* app = [parameters objectForKey:@"app"];
    NSString* state = [parameters objectForKey:@"state"];
    NSString* created = [parameters objectForKey:@"created"];
    NSString* issuer = [parameters objectForKey:@"issuer"];
    NSString* username = [parameters objectForKey:@"username"];
    oneStep = username == nil ? YES : NO;
    
    if (app != nil && state != nil && created != nil && issuer != nil){
        OxPush2Request* oxRequest = [[OxPush2Request alloc] initWithName:username app:app issuer:issuer state:state method:@"GET" created:created];
        NSMutableDictionary* parameters = [[NSMutableDictionary alloc] init];
        [parameters setObject:[oxRequest app] forKey:@"application"];
        [parameters setObject:[oxRequest state] forKey:@"session_state"];
        if (!oneStep){
            [parameters setObject:[oxRequest userName] forKey:@"username"];
        }
        [[ApiServiceManager sharedInstance] doRequest:oxRequest callback:^(NSDictionary *result,NSError *error){
            if (error) {
                [self handleError:error];
            } else {
                // Success getting U2fMetaData
                NSString* version = [result objectForKey:@"version"];
                NSString* issuer = [result objectForKey:@"issuer"];
                NSString* authenticationEndpoint = [result objectForKey:@"authentication_endpoint"];
                NSString* registrationEndpoint = [result objectForKey:@"registration_endpoint"];
                U2fMetaData* u2fMetaData = [[U2fMetaData alloc] initWithVersion:version issuer:issuer authenticationEndpoint:authenticationEndpoint registrationEndpoint:registrationEndpoint];
                // Next step - get exist keys from database
                NSString* keyID = [NSString stringWithFormat:@"%@%@", [oxRequest issuer], [oxRequest app]];
                NSArray* tokenEntities = [[DataStoreManager sharedInstance] getTokenEntitiesByID:keyID];
                NSString* u2fEndpoint = [[NSString alloc] init];
                BOOL isEnroll = [tokenEntities count] > 0 ? NO : YES;
                if (!isEnroll){//authentication
                    u2fEndpoint = [u2fMetaData authenticationEndpoint];
                } else {//registration
                    u2fEndpoint = [u2fMetaData registrationEndpoint];
                }
                if (!oneStep && [tokenEntities count] > 0){
                    __block BOOL isResult = NO;
                    for (TokenEntity* tokenEntity in tokenEntities){
                        NSString* kHandle = [tokenEntity keyHandle];
//                        NSString* kHandleURLEncode = [kHandle URLEncode];
                        if (kHandle != nil){
                            [parameters setObject:kHandle forKey:@"keyhandle"];
                            [[ApiServiceManager sharedInstance] doGETUrl:u2fEndpoint :parameters callback:^(NSDictionary *result,NSError *error){
                                if (error) {
                                    [self handleError:error];
                                    [[DataStoreManager sharedInstance] deleteTokenEntitiesByID:@""];
                                    [self postNotificationFailedKeyHandle];
                                } else {
                                    // Success
//                                    NSLog(@"Success - %@", result);
                                    isResult = YES;
                                    [self postNotificationAutenticationStarting];
                                    [self callServiceChallenge:u2fEndpoint isEnroll:isEnroll andParameters:parameters];
                                }
                            }];
                            if (isResult)break;
                        } else{
                            break;
                        }
                    }
                } else {
                    [self postNotificationEnrollementStarting];
                    [self callServiceChallenge:u2fEndpoint isEnroll:isEnroll andParameters:parameters];
                }
            }
        }];
    }
}

-(void)callServiceChallenge:(NSString*)baseUrl isEnroll:(BOOL)isEnroll andParameters:(NSDictionary*)parameters{
    [[ApiServiceManager sharedInstance] doGETUrl:baseUrl :parameters callback:^(NSDictionary *result,NSError *error){
        if (error) {
            [self postNotificationAutenticationFailed];
        } else {
            // Success getting authenticate MetaData
            [self onChallengeReceived:baseUrl isEnroll:isEnroll metaData:result];
//            [self postNotificationAutenticationSuccess];
        }
    }];
}

-(void)onChallengeReceived:(NSString*)baseUrl isEnroll:(BOOL)isEnroll metaData:(NSDictionary*)result{
    TokenResponse* tokenResponce;
    TokenManager* tokenManager = [[TokenManager alloc] init];
    if (isEnroll){
        [self postNotificationEnrollementStarting];
        tokenResponce = [tokenManager enroll:result baseUrl:baseUrl];
    }
    if (tokenResponce == nil){
        tokenResponce = [tokenManager sign:result baseUrl:baseUrl];
    }
    NSMutableDictionary* tokenParameters = [[NSMutableDictionary alloc] init];
    [tokenParameters setObject:@"username" forKey:@"username"];
    [tokenParameters setObject:[tokenResponce response] forKey:@"tokenResponse"];
    [self callServiceAuthenticateToken:baseUrl andParameters:tokenParameters];
}

-(void)callServiceAuthenticateToken:(NSString*)baseUrl andParameters:(NSDictionary*)parameters{
    [[ApiServiceManager sharedInstance] callPOSTMultiPartAPIService:baseUrl andParameters:parameters];
}

-(void)postNotificationAutenticationStarting{
    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_AUTENTIFICATION_STARTING object:nil userInfo:[self getStep]];
}

-(void)postNotificationAutenticationSuccess{
    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_AUTENTIFICATION_SUCCESS object:nil userInfo:[self getStep]];
}

-(void)postNotificationAutenticationFailed{
    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_AUTENTIFICATION_FAILED object:nil userInfo:[self getStep]];
}

-(void)postNotificationEnrollementStarting{
    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_REGISTRATION_STARTING object:nil userInfo:[self getStep]];
}

-(void)postNotificationEnrollementSuccess{
    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_REGISTRATION_SUCCESS object:nil userInfo:[self getStep]];
}

-(void)postNotificationEnrollementFailed{
    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_REGISTRATION_FAILED object:nil userInfo:[self getStep]];
}

-(void)postNotificationFailedKeyHandle{
    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_FAILED_KEYHANDLE object:nil userInfo:[self getStep]];
}

-(void)handleError:(NSError*)error{
    NSData* errrorr = [error.userInfo objectForKey:@"com.alamofire.serialization.response.error.data"];
    NSString* errorDescription;
    if (errrorr != nil){
        NSDictionary *jsonErrorObject = [NSJSONSerialization JSONObjectWithData:errrorr options: NSJSONReadingMutableLeaves error:nil];
        errorDescription = [jsonErrorObject valueForKey:@"errorDescription"];
    } else {
        errorDescription = [error localizedDescription];
    }
    
    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_ERROR object:errorDescription];
    
    NSLog(@"Error - %@", error);
}

-(NSDictionary*)getStep{
    NSDictionary* userInfo = @{@"oneStep": @(oneStep)};
    return userInfo;
}

@end
