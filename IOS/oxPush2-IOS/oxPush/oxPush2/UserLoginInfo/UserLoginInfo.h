//
//  UserLoginInfo.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/16/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface UserLoginInfo : NSObject

typedef NS_ENUM(int, LogState) {
    LOGIN_SUCCESS = 0,
    LOGIN_FAILED = 1,
    LOGIN_DECLINED = 2,
    ENROLL_SUCCESS = 3,
    ENROLL_FAILED = 4,
    ENROLL_DECLINED = 5,
    UNKNOWN_ERROR = 6
};

+ (instancetype) sharedInstance;

@property (strong, nonatomic) NSString* userName;
@property (strong, nonatomic) NSString* created;
@property (strong, nonatomic) NSString* application;
@property (strong, nonatomic) NSString* issuer;
@property (strong, nonatomic) NSString* authenticationType;
@property (strong, nonatomic) NSString* authenticationMode;
@property (strong, nonatomic) NSString* locationIP;
@property (strong, nonatomic) NSString* locationCity;
@property (assign, nonatomic) LogState logState;
@property (strong, nonatomic) NSString* errorMessage;

@end
