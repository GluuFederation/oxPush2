//
//  U2fMetaData.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/3/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "U2fMetaData.h"

@implementation U2fMetaData

-(id)initWithVersion:(NSString*)version issuer:(NSString*)issuer authenticationEndpoint:(NSString*)authenticationEndpoint registrationEndpoint:(NSString*)registrationEndpoint{

    _version = version;
    _issuer = issuer;
    _authenticationEndpoint = authenticationEndpoint;
    _registrationEndpoint = registrationEndpoint;
    
    return self;
}

@end
