//
//  ApiService.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/2/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ApiService : NSObject

+ (instancetype) sharedInstance;

typedef void(^RequestCompletionHandler)(NSDictionary *result, NSError *error);

- (void)doGET:(NSString *)baseUrl parameters:(NSDictionary *)parameters callback:(RequestCompletionHandler)handler;

- (void)doPOST:(NSString *)baseUrl parameters:(NSDictionary *)parameters callback:(RequestCompletionHandler)handler;

-(void)callPOSTMultiPartAPIService:(NSString*)url andParameters:(NSDictionary*)parameters isDecline:(BOOL)isDecline callback:(RequestCompletionHandler)handler;

@end
