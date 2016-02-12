//
//  EnrollmentResponse.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/3/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface EnrollmentResponse : NSObject

@property (strong, nonatomic) NSData* userPublicKey;
@property (strong, nonatomic) NSData* keyHandle;
@property (strong, nonatomic) NSData* attestationCertificate;
@property (strong, nonatomic) NSData* signature;

-(id)initWithUserPublicKey:(NSData*)userPublicKey keyHandle:(NSData*)keyHandle attestationCertificate:(NSData*)attestationCertificate signature:(NSData*)signature;

@end
