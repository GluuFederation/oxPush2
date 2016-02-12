//
//  TokenManager.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/3/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TokenResponse.h"
#import "RawMessageCodec.h"
#import "U2FKeyImpl.h"

@interface TokenManager : NSObject{

    RawMessageCodec* codec;
    U2FKeyImpl* u2FKey;
}

-(TokenResponse*)enroll:(NSDictionary*)parameters baseUrl:(NSString*)baseUrl;
-(TokenResponse*)sign:(NSDictionary*)parameters baseUrl:(NSString*)baseUrl;

@end
