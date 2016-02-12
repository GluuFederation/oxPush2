//
//  RawMessageCodec.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/12/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "EnrollmentResponse.h"
#import "AuthenticateResponse.h"

@interface RawMessageCodec : NSObject

-(NSData*)encodeEnrollementSignedBytes:(Byte)reservedByte applicationSha256:(NSData*)applicationSha256 challengeSha256:(NSData*)challengeSha256 keyHandle:(NSData*)keyHandle userPublicKey:(NSData*)userPublicKey;

-(NSData*)encodeAuthenticateSignedBytes:(NSData*)applicationSha256 userPresence:(NSData*)userPresence counter:(int)counter challengeSha256:(NSData*)challengeSha256;

-(NSData*)encodeRegisterResponse:(EnrollmentResponse*)enrollmentResponse;

-(NSData*)encodeAuthenticateResponse:(AuthenticateResponse*)authenticateResponse;

@end
