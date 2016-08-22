//
//  ApiServiceManager.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/2/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "ApiServiceManager.h"
#import "ApiService.h"

@implementation ApiServiceManager

+ (instancetype) sharedInstance {
    static id instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

- (void)doRequest:(OxPush2Request *)oxRequest callback:(RequestCompletionHandler)handler{
    NSString* method = [oxRequest method];
    NSString* discoveryUrl = [oxRequest issuer];
    discoveryUrl = [discoveryUrl stringByAppendingString:@"/.well-known/fido-u2f-configuration"];
    NSMutableDictionary* parameters = [[NSMutableDictionary alloc] init];
    [parameters setObject:[oxRequest app] forKey:@"app"];
    [parameters setObject:[oxRequest state] forKey:@"state"];
    [parameters setObject:[oxRequest created] forKey:@"created"];
    [parameters setObject:[oxRequest issuer] forKey:@"issuer"];
    if ([method isEqualToString:@"GET"]){
        [self doGETUrl:discoveryUrl :parameters callback:handler];
    } else if ([method isEqualToString:@"POST"]){
        [self doPOSTUrl:discoveryUrl :parameters callback:handler];
    }
}

-(void)doGETUrl:(NSString*)baseURl :(NSDictionary *)parameters callback:(RequestCompletionHandler)handler{
    [[ApiService sharedInstance] doGET:baseURl parameters:parameters callback:^(NSDictionary *result,NSError *error){
        if (error) {
            handler(nil , error);
        } else {
            //Success
            handler(result ,nil);
        }
    }];
}

-(void)doPOSTUrl:(NSString*)baseURl :(NSDictionary *)parameters callback:(RequestCompletionHandler)handler{
    [[ApiService sharedInstance] doPOST:baseURl parameters:parameters callback:^(NSDictionary *result,NSError *error){
        if (error) {
            handler(nil , error);
        } else {
            //Success
            handler(result ,nil);
        }
    }];
}

-(void)callPOSTMultiPartAPIService:(NSString*)url andParameters:(NSDictionary*)parameters isDecline:(BOOL)isDecline callback:(RequestCompletionHandler)handler{
    [[ApiService sharedInstance] callPOSTMultiPartAPIService:url andParameters:parameters isDecline:isDecline callback:^(NSDictionary *result,NSError *error){
        if (error) {
            handler(nil , error);
        } else {
            //Success
            handler(result ,nil);
        }
    }];
}

@end
