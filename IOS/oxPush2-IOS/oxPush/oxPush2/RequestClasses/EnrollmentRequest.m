//
//  EnrollmentRequest.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/3/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "EnrollmentRequest.h"

@implementation EnrollmentRequest

-(id)initWithVersion:(NSString*)version challenge:(NSString*)challenge application:(NSString*)application issuer:(NSString*)issuer{

    _version = version;
    _challenge = challenge;
    _application = application;
    _issuer = issuer;
    
    return self;
}

@end
