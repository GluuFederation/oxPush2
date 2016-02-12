//
//  TokenEntity.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/3/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface TokenEntity : NSObject

@property (strong, nonatomic) NSString* ID;
@property (strong, nonatomic) NSString* application;
@property (strong, nonatomic) NSString* issuer;
@property (strong, nonatomic) NSString* privateKey;
@property (strong, nonatomic) NSString* publicKey;
@property (strong, nonatomic) NSString* keyHandle;
@property (assign, nonatomic) int count;

-(id)initWithID:(NSString*)ID privateKey:(NSData*)privateKey publicKey:(NSData*)publicKey;

@end
