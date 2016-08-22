//
//  EnrollmentRequest.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/3/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface EnrollmentRequest : NSObject

@property (strong, nonatomic) NSString* version;
@property (strong, nonatomic) NSString* challenge;
@property (strong, nonatomic) NSString* application;
@property (strong, nonatomic) NSString* issuer;

-(id)initWithVersion:(NSString*)version challenge:(NSString*)challenge application:(NSString*)application issuer:(NSString*)issuer;

@end
