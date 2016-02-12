//
//  QRRequest.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/2/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface OxPush2Request : NSObject

@property (strong, nonatomic) NSString* userName;
@property (strong, nonatomic) NSString* app;
@property (strong, nonatomic) NSString* issuer;
@property (strong, nonatomic) NSString* state;
@property (strong, nonatomic) NSString* method;
@property (strong, nonatomic) NSString* created;

-(id)initWithName:(NSString*)name app:(NSString*)app issuer:(NSString*)issuer state:(NSString*)state method:(NSString*)method created:(NSString*)created;

@end
