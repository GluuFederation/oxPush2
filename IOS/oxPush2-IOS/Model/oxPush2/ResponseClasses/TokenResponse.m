//
//  TokenResponse.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/3/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "TokenResponse.h"

@implementation TokenResponse

-(id)initWithResponce:(NSString*)responce challenge:(NSString*)challenge keyHandle:(NSString*)keyHandle{
    
    _response = responce;
    _challenge = challenge;
    _keyHandle = keyHandle;
    
    return self;
}

@end
