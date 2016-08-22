//
//  AuthenticateResponse.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/3/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "AuthenticateResponse.h"

@implementation AuthenticateResponse

-(id)initWithUserPresence:(NSData*)userPresence counter:(int)counter signature:(NSData*)signature{

    _userPresence = userPresence;
    _counter = counter;
    _signature = signature;
    
    return self;
}

@end
