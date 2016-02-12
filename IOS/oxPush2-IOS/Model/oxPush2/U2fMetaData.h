//
//  U2fMetaData.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/3/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface U2fMetaData : NSObject

@property (strong, nonatomic) NSString* version;
@property (strong, nonatomic) NSString* issuer;
@property (strong, nonatomic) NSString* authenticationEndpoint;
@property (strong, nonatomic) NSString* registrationEndpoint;

-(id)initWithVersion:(NSString*)version issuer:(NSString*)issuer authenticationEndpoint:(NSString*)authenticationEndpoint registrationEndpoint:(NSString*)registrationEndpoint;

@end
