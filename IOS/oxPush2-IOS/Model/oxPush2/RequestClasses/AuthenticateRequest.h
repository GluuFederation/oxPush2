//
//  AuthenticateRequest.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/11/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface AuthenticateRequest : NSObject

@property (strong, nonatomic) NSString* version;
@property (strong, nonatomic) NSData* control;
@property (strong, nonatomic) NSString* challenge;
@property (strong, nonatomic) NSString* application;
@property (strong, nonatomic) NSData* keyHandle;

-(id)initWithVersion:(NSString*)version control:(NSData*)control challenge:(NSString*)challenge application:(NSString*)application keyHandle:(NSData*)keyHandle;
@end
