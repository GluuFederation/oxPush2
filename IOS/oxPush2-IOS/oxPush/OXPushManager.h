//
//  OXPushManager.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/9/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface OXPushManager : NSObject{

    BOOL oneStep;
    
    NSString* deviceToken;
}

typedef void(^RequestCompletionHandler)(NSDictionary *result, NSError *error);

-(void)onOxPushApproveRequest:(NSDictionary*)parameters isDecline:(BOOL)isDecline callback:(RequestCompletionHandler)handler;

-(void)setDevicePushToken:(NSString*)deviceToken;
-(NSArray*)getLogs;
-(NSArray*)getKeys;

@end
