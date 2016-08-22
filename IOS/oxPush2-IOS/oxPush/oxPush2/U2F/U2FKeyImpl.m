//
//  U2FKeyImpl.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/12/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "U2FKeyImpl.h"
#import "GMEllipticCurveCrypto.h"
#import "GMEllipticCurveCrypto+hash.h"
#import "TokenEntity.h"
#import <NSHash/NSString+NSHash.h>
#import <NSHash/NSData+NSHash.h>
#import "Base64.h"
#import "Constants.h"
#import "DataStoreManager.h"
#import "NSString+URLEncode.h"
#import "AuthenticateResponse.h"
#import "AuthenticateRequest.h"
#import "UserPresenceVerifier.h"

Byte REGISTRATION_RESERVED_BYTE_VALUE = 0x05;
int keyHandleLength = 64;

@implementation U2FKeyImpl

-(id)init{
    codec = [[RawMessageCodec alloc] init];
    return self;
}

-(EnrollmentResponse*) registerRequest:(EnrollmentRequest*)enrollmentRequest isDecline:(BOOL)isDecline{
    
    NSString* application = [enrollmentRequest application];
    NSString* challenge = [enrollmentRequest challenge];
    
    //Generate a new ECC key pair
    GMEllipticCurveCrypto *crypto = [GMEllipticCurveCrypto generateKeyPairForCurve:
                                     GMEllipticCurveSecp256r1];
    
    NSData* userPublicKey = crypto.publicKey;
    NSMutableData* keyHandle = [[NSMutableData alloc] init];
    for (int i = 0; i < keyHandleLength; i ++){
        int randomByte = arc4random() % 256;
        [keyHandle appendBytes:&randomByte length:1];
    }
    if (!isDecline){
        //Save new keys into database
        TokenEntity* newTokenEntity = [[TokenEntity alloc] init];
        NSString* keyID = application;
        [newTokenEntity setID:keyID];
        [newTokenEntity setApplication:application];
        [newTokenEntity setIssuer:[enrollmentRequest issuer]];
        [newTokenEntity setKeyHandle:[keyHandle base64EncodedString]];
        [newTokenEntity setPublicKey:crypto.publicKeyBase64];
        [newTokenEntity setPrivateKey:crypto.privateKeyBase64];
        [newTokenEntity setUserName:[[UserLoginInfo sharedInstance] userName]];
        [newTokenEntity setPairingTime:[[UserLoginInfo sharedInstance] created]];
        [newTokenEntity setAuthenticationMode:[[UserLoginInfo sharedInstance] authenticationMode]];
        [newTokenEntity setAuthenticationType:[[UserLoginInfo sharedInstance] authenticationType]];
        [[DataStoreManager sharedInstance] saveTokenEntity:newTokenEntity];
    }
    
    NSData* applicationSha256 = [[application SHA256] dataUsingEncoding:NSUTF8StringEncoding];
    NSData* challengeSha256 = [[challenge SHA256] dataUsingEncoding:NSUTF8StringEncoding];
    
    NSData* signedData = [codec encodeEnrollementSignedBytes:REGISTRATION_RESERVED_BYTE_VALUE applicationSha256:applicationSha256 challengeSha256:challengeSha256 keyHandle:keyHandle userPublicKey:userPublicKey];
    
    NSData *signature = [crypto hashSHA256AndSignDataEncoded:signedData];
    
    NSData* sertificate = [VENDOR_CERTIFICATE_CERT dataFromHexString];
    
    EnrollmentResponse* responce = [[EnrollmentResponse alloc] initWithUserPublicKey:userPublicKey keyHandle:keyHandle attestationCertificate:sertificate signature:signature];
    
    return responce;
}

-(AuthenticateResponse*)autenticate:(AuthenticateRequest*)request{
    
    //    NSData* control = [request control];
    NSString* application = [request application];
    NSString* challenge = [request challenge];
    //    NSData* keyHandle = [request keyHandle];
    TokenEntity* tokenEntity = nil;
    NSArray* tokenEntities = [[DataStoreManager sharedInstance] getTokenEntitiesByID:application];
    if ([tokenEntities count] > 0){
        tokenEntity = [tokenEntities objectAtIndex:0];
    } else {
        //No token(s) found
    }
    if (tokenEntity != nil){
        
    } else {
        //There is no keyPair
    }
    int count = [[DataStoreManager sharedInstance] incrementCountForToken:tokenEntity];
    UserPresenceVerifier* userPres = [[UserPresenceVerifier alloc] init];
    NSData* userPresence = [userPres verifyUserPresence];
    NSData* applicationSha256 = [[application SHA256] dataUsingEncoding:NSUTF8StringEncoding];
    NSData* challengeSha256 = [[challenge SHA256] dataUsingEncoding:NSUTF8StringEncoding];
    
    NSData* signedData = [codec encodeAuthenticateSignedBytes:applicationSha256 userPresence:userPresence counter:count challengeSha256:challengeSha256];
    
    GMEllipticCurveCrypto* crypto2 = [GMEllipticCurveCrypto generateKeyPairForCurve:
                                      GMEllipticCurveSecp256r1];
    
    NSData *signature = [crypto2 hashSHA256AndSignDataEncoded:signedData];
    
    return [[AuthenticateResponse alloc] initWithUserPresence:userPresence counter:count signature:signature];
}


@end
