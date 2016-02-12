//
//  RawMessageCodec.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/12/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "RawMessageCodec.h"

@implementation RawMessageCodec

-(NSData*)encodeEnrollementSignedBytes:(Byte)reservedByte applicationSha256:(NSData*)applicationSha256 challengeSha256:(NSData*)challengeSha256 keyHandle:(NSData*)keyHandle userPublicKey:(NSData*)userPublicKey {
    
    NSMutableData* signedData = [[NSMutableData alloc] init];
    [signedData appendBytes:&reservedByte length:1];
    [signedData appendData:applicationSha256];
    [signedData appendData:challengeSha256];
    [signedData appendData:keyHandle];
    [signedData appendData:userPublicKey];
    
    return signedData;
}

-(NSData*)encodeAuthenticateSignedBytes:(NSData*)applicationSha256 userPresence:(NSData*)userPresence counter:(int)counter challengeSha256:(NSData*)challengeSha256{
    
    NSMutableData* signedData = [[NSMutableData alloc] init];
    [signedData appendData:applicationSha256];
    [signedData appendData:userPresence];
    [signedData appendBytes:&counter length:sizeof(counter)];
    [signedData appendData:challengeSha256];
    
    return signedData;
}

-(NSData*)encodeRegisterResponse:(EnrollmentResponse*)enrollmentResponse{
    Byte REGISTRATION_RESERVED_BYTE_VALUE = 0x05;
    
    NSMutableData* result = [[NSMutableData alloc] init];
    int keyHandleLength = (int)[[enrollmentResponse keyHandle] length];
    [result appendBytes:&REGISTRATION_RESERVED_BYTE_VALUE length:1];
    [result appendData:[enrollmentResponse userPublicKey]];
    [result appendBytes:&keyHandleLength length:1];
    [result appendData:[enrollmentResponse keyHandle]];
    [result appendData:[enrollmentResponse attestationCertificate]];
    [result appendData:[enrollmentResponse signature]];
    
    return result;
}

-(NSData*)encodeAuthenticateResponse:(AuthenticateResponse*)authenticateResponse{
    int c = authenticateResponse.counter;
    NSMutableData* resp = [[NSMutableData alloc] init];
    [resp appendData:authenticateResponse.userPresence];
    [resp appendBytes:&c length:4];
    [resp appendData:authenticateResponse.signature];
    
    return resp;
}

@end
