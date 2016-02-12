//
//  LogManager.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/12/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "LogManager.h"

@implementation LogManager

+ (instancetype) sharedInstance {
    static id instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

-(void)addLog:(NSString*)logs{
    NSString* log = [[NSUserDefaults standardUserDefaults] stringForKey:@"LOGS"];
    if (log != nil){
        log = [log stringByAppendingString:[NSString stringWithFormat:@"\n %@ %@", [NSDate date], logs]];
    } else {
        log = logs;
    }
    [[NSUserDefaults standardUserDefaults] setObject:log forKey:@"LOGS"];
}

-(NSString*)getLogs{
    NSString* logs = [[NSUserDefaults standardUserDefaults] stringForKey:@"LOGS"];
    
    return logs;
}

@end
