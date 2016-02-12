//
//  U2FKeyImpl.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/12/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "EnrollmentRequest.h"
#import "EnrollmentResponse.h"
#import "RawMessageCodec.h"
#import "AuthenticateRequest.h"

@interface U2FKeyImpl : NSObject{
    
    RawMessageCodec* codec;
}

-(EnrollmentResponse*)registerRequest:(EnrollmentRequest*)request;
-(AuthenticateResponse*)autenticate:(AuthenticateRequest*)request;

@end
