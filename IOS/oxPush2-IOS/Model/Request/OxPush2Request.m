//
//  QRRequest.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/2/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "OxPush2Request.h"

@implementation OxPush2Request

-(id)initWithName:(NSString*)name app:(NSString*)app issuer:(NSString*)issuer state:(NSString*)state method:(NSString*)method created:(NSString*)created{
    
    _userName = name;
    _app = app;
    _issuer = issuer;
    _state = state;
    _method = method;
    _created = created;
    
    return self;
}

@end
