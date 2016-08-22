//
//  ApiServiceManager.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/2/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "OxPush2Request.h"

@interface ApiServiceManager : NSObject

+ (instancetype) sharedInstance;

typedef void(^RequestCompletionHandler)(NSDictionary *result, NSError *error);

- (void)doRequest:(OxPush2Request *)oxRequest callback:(RequestCompletionHandler)handler;
- (void)doGETUrl:(NSString*)baseURl :(NSDictionary *)parameters callback:(RequestCompletionHandler)handler;
- (void)doPOSTUrl:(NSString*)baseURl :(NSDictionary *)parameters callback:(RequestCompletionHandler)handler;

-(void)callPOSTMultiPartAPIService:(NSString*)url andParameters:(NSDictionary*)parameters isDecline:(BOOL)isDecline callback:(RequestCompletionHandler)handler;

@end
