//
//  ECCKey.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/3/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "TokenEntity.h"

@implementation TokenEntity

-(id)initWithID:(NSString*)ID privateKey:(NSString*)privateKey publicKey:(NSString*)publicKey{

    _ID = ID;
    _privateKey = privateKey;
    _publicKey = publicKey;
    
    return self;
}

@end
