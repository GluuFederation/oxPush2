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
}

-(void)onOxPushApproveRequest:(NSDictionary*)parameters;

@end
