//
//  TokenResponse.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/3/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface TokenResponse : NSObject

@property (strong, nonatomic) NSString* response;
@property (strong, nonatomic) NSString* challenge;
@property (strong, nonatomic) NSString* keyHandle;

-(id)initWithResponce:(NSString*)responce challenge:(NSString*)challenge keyHandle:(NSString*)keyHandle;

@end
