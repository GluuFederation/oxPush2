//
//  UserLoginInfo.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/16/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface UserLoginInfo : NSObject

+ (instancetype) sharedInstance;

@property (strong, nonatomic) NSString* userName;
@property (strong, nonatomic) NSString* created;
@property (strong, nonatomic) NSString* application;
@property (strong, nonatomic) NSString* issuer;
@property (strong, nonatomic) NSString* authenticationType;
@property (strong, nonatomic) NSString* authenticationMode;

@end
