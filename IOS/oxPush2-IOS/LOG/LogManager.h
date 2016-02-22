//
//  LogManager.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/12/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface LogManager : NSObject

+ (instancetype) sharedInstance;

-(void)addLog:(NSString*)logs;
-(NSString*)getLogs;
-(void)deleteAllLogs;

@end
