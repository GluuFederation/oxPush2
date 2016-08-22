//
//  AuthenticateRequest.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/11/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "AuthenticateRequest.h"

@implementation AuthenticateRequest

-(id)initWithVersion:(NSString*)version control:(NSData*)control challenge:(NSString*)challenge application:(NSString*)application keyHandle:(NSData*)keyHandle{

    _version = version;
    _control = control;
    _challenge = challenge;
    _application = application;
    _keyHandle = keyHandle;
    
    return self;
}

@end
