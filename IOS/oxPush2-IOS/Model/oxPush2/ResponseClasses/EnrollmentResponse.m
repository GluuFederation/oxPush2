//
//  EnrollmentResponse.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/3/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "EnrollmentResponse.h"

@implementation EnrollmentResponse

-(id)initWithUserPublicKey:(NSData*)userPublicKey keyHandle:(NSData*)keyHandle attestationCertificate:(NSData*)attestationCertificate signature:(NSData*)signature{
    
    _userPublicKey = userPublicKey;
    _keyHandle = [keyHandle copy];
    _attestationCertificate = attestationCertificate;
    _signature = signature;
    
    return self;
}

@end
