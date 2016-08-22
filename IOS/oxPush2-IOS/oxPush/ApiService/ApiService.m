//
//  ApiService.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/2/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "ApiService.h"
#import "AFHTTPRequestOperationManager.h"
#import "Constants.h"
#import "LogManager.h"
#import "DataStoreManager.h"

@implementation ApiService

+ (instancetype) sharedInstance {
    static id instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

- (void)doGET:(NSString *)baseUrl parameters:(NSDictionary *)parameters callback:(RequestCompletionHandler)handler{

    [self callGETAPIService:baseUrl andParameters:parameters andCallback:handler];
}

- (void)doPOST:(NSString *)baseUrl parameters:(NSDictionary *)parameters callback:(RequestCompletionHandler)handler{
    
    [self callPOSTAPIService:baseUrl andParameters:parameters andCallback:handler];
}

//---------------------- HTTP REQUEST MANAGER ---------------------------------

-(AFHTTPRequestOperationManager*)getAFHTTPRequestManager{
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    manager.responseSerializer = [AFJSONResponseSerializer
                                  serializerWithReadingOptions:NSJSONReadingAllowFragments];
    manager.responseSerializer.acceptableContentTypes = [NSSet setWithObject:@"application/json"];//x-www-form-urlencoded"];
    
    /**** SSL Pinning ****/
    NSString *cerPath = [[NSBundle mainBundle] pathForResource:@"super-gluu-ssl" ofType:@"crt"];
    NSData *certData = [NSData dataWithContentsOfFile:cerPath];
    AFSecurityPolicy *securityPolicy = [AFSecurityPolicy policyWithPinningMode:AFSSLPinningModeCertificate];
    [securityPolicy setAllowInvalidCertificates:NO];
    [securityPolicy setPinnedCertificates:@[certData]];
    /**** SSL Pinning ****/
    
//    [manager setSecurityPolicy:securityPolicy];
    manager.securityPolicy.allowInvalidCertificates = YES;
    [manager.securityPolicy setValidatesDomainName:NO];
    return manager;
}

-(void)callGETAPIService:(NSString*)url andParameters:(NSDictionary*)parameters andCallback:(RequestCompletionHandler)handler{
    AFHTTPRequestOperationManager *manager = [self getAFHTTPRequestManager];
    
    [manager GET:url parameters:parameters success:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSLog(@"JSON: %@", responseObject);
        handler(responseObject ,nil);
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        NSLog(@"Error: %@", error);
        handler(nil , error);
    }];
    
}

-(void)callPOSTAPIService:(NSString*)url andParameters:(NSDictionary*)parameters andCallback:(RequestCompletionHandler)handler{
    
//    AFHTTPRequestOperationManager *manager = [self getAFHTTPRequestManager];

    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    manager.responseSerializer = [AFJSONResponseSerializer
                                  serializerWithReadingOptions:NSJSONReadingAllowFragments];
    manager.responseSerializer.acceptableContentTypes = [NSSet setWithObject:@"application/x-www-form-urlencoded"];//x-www-form-urlencoded"];//application/json
    
    [manager POST:url parameters:parameters success:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSLog(@"JSON: %@", responseObject);
        handler(responseObject ,nil);
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        NSLog(@"Error: %@", error);
        handler(nil , error);
    }];
    
}

//---------------------- END OF HTTP REQUEST MANAGER ---------------------------------

-(void)callPOSTMultiPartAPIService:(NSString*)url andParameters:(NSDictionary*)parameters isDecline:(BOOL)isDecline callback:(RequestCompletionHandler)handler{
    BOOL isEnroll = [url rangeOfString:@"registration"].location != NSNotFound ? YES : NO;
    // the server url to which the image (or the media) is uploaded. Use your server url here
    NSURL *baseUrl = [NSURL URLWithString:url];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:baseUrl];
    [request setHTTPMethod:@"POST"];
    NSString *contentType = @"application/x-www-form-urlencoded";
    [request addValue:contentType forHTTPHeaderField: @"Content-Type"];
    
    NSMutableData *body = [NSMutableData data];
    [body appendData:[[NSString stringWithFormat:@"username=%@", @""] dataUsingEncoding:NSUTF8StringEncoding]];
    [body appendData:[[NSString stringWithFormat:@"&tokenResponse=%@", [parameters objectForKey:@"tokenResponse"]] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setHTTPBody:body];

    NSHTTPURLResponse* response = nil;
    NSError* error;
    
//    NSURLSession *session = [NSURLSession sharedSession];
//    NSURLSessionDataTask* returnData = [session dataTaskWithRequest:request];
//    
//    NSLog(@"returnData - %@", returnData);
    
    NSData* urlData = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
//    NSLog(@"%@", response); //300
    [[UserLoginInfo sharedInstance] setCreated:[NSString stringWithFormat:@"%@", [NSDate date]]];
    if (response != nil && [response statusCode] >=200 && [response statusCode] <300){
        NSDictionary *jsonData = [NSJSONSerialization JSONObjectWithData:urlData options:kNilOptions error:&error];
        handler(jsonData, nil);
        if([[jsonData objectForKey:@"status"] isEqualToString:@"success"])
        {
            if (isDecline){
                if (isEnroll){
                    [[UserLoginInfo sharedInstance] setLogState:ENROLL_DECLINED];
                    [[UserLoginInfo sharedInstance] setErrorMessage:@"Enrol was declined"];
                    [[DataStoreManager sharedInstance] saveUserLoginInfo:[UserLoginInfo sharedInstance]];
                } else {
                    [[UserLoginInfo sharedInstance] setLogState:LOGIN_DECLINED];
                    [[UserLoginInfo sharedInstance] setErrorMessage:@"Login was declined"];
                    [[DataStoreManager sharedInstance] saveUserLoginInfo:[UserLoginInfo sharedInstance]];
                }
//                [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_DECLINE_SUCCESS object:urlData];
            } else {
                if (isEnroll){
                    [[UserLoginInfo sharedInstance] setLogState:ENROLL_SUCCESS];
                    [[DataStoreManager sharedInstance] saveUserLoginInfo:[UserLoginInfo sharedInstance]];
//                    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_REGISTRATION_SUCCESS object:urlData];
                } else {
                    [[UserLoginInfo sharedInstance] setLogState:LOGIN_SUCCESS];
                    [[DataStoreManager sharedInstance] saveUserLoginInfo:[UserLoginInfo sharedInstance]];
//                    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_AUTENTIFICATION_SUCCESS object:urlData];
                }
            }
        }else{
            handler(nil, error);
//                if (isEnroll){
//                    [[UserLoginInfo sharedInstance] setLogState:ENROLL_FAILED];
//                    [[DataStoreManager sharedInstance] saveUserLoginInfo:[UserLoginInfo sharedInstance]];
//                } else {
//                    [[UserLoginInfo sharedInstance] setLogState:LOGIN_FAILED];
//                    [[DataStoreManager sharedInstance] saveUserLoginInfo:[UserLoginInfo sharedInstance]];
//                }
//                [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_DECLINE_FAILED object:nil];
//            } else {
                if (isEnroll){
                    [[UserLoginInfo sharedInstance] setLogState:ENROLL_FAILED];
                    [[DataStoreManager sharedInstance] saveUserLoginInfo:[UserLoginInfo sharedInstance]];
//                    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_REGISTRATION_FAILED object:nil];
                } else {
                    [[UserLoginInfo sharedInstance] setLogState:LOGIN_FAILED];
                    [[DataStoreManager sharedInstance] saveUserLoginInfo:[UserLoginInfo sharedInstance]];
//                    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_AUTENTIFICATION_FAILED object:nil];
                }
        }
    } else{
        handler(nil, error);
        NSString* erStr = [[NSString alloc] initWithData:urlData encoding:NSUTF8StringEncoding];
        NSLog(@"ERROR MESSAGE - %@", erStr);
        NSError* error;
        NSDictionary* jsonError = [NSJSONSerialization JSONObjectWithData:urlData
                                                             options:kNilOptions
                                                               error:&error];
        NSString* errroMessage = @"";
        if (jsonError != nil){
            NSString* reason = [jsonError valueForKey:@"error_description"];
            if (reason != nil){
                errroMessage = reason;
            } else {
                errroMessage = erStr;
            }
        } else {
            errroMessage = erStr;
        }

        if (isDecline){
//            [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_DECLINE_FAILED object:nil];
        } else {
            if (isEnroll){
                [[UserLoginInfo sharedInstance] setLogState:ENROLL_FAILED];
                [[UserLoginInfo sharedInstance] setErrorMessage:errroMessage];
                [[DataStoreManager sharedInstance] saveUserLoginInfo:[UserLoginInfo sharedInstance]];
                [[UserLoginInfo sharedInstance] setErrorMessage:@""];
//                [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_REGISTRATION_FAILED object:nil];
            } else {
                [[UserLoginInfo sharedInstance] setLogState:LOGIN_FAILED];
                [[UserLoginInfo sharedInstance] setErrorMessage:errroMessage];
                [[DataStoreManager sharedInstance] saveUserLoginInfo:[UserLoginInfo sharedInstance]];
                [[UserLoginInfo sharedInstance] setErrorMessage:@""];
//                [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_AUTENTIFICATION_FAILED object:nil];
            }
        }
    }

//    NSString* code = [responce ];
    // {"status":"success","challenge":"gkJaeu9_frj72yQ04RYZxajzz2Kg9s4YLht52WY0_S4"}
}

@end
