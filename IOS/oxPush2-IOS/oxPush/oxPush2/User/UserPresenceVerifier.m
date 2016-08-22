//
//  UserPresenceVerifier.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/11/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "UserPresenceVerifier.h"

@implementation UserPresenceVerifier

-(NSData*)verifyUserPresence{
    int byte = 1;
    return [[NSData alloc] initWithBytes:&byte length:1];
}

@end
