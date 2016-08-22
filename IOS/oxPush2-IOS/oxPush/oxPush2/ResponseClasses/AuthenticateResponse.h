//
//  AuthenticateResponse.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/3/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface AuthenticateResponse : NSObject

@property (strong, nonatomic) NSData* userPresence;
@property (assign, nonatomic) int counter;
@property (strong, nonatomic) NSData* signature;

-(id)initWithUserPresence:(NSData*)userPresence counter:(int)counter signature:(NSData*)signature;

@end
