//
//  LogsTableCell.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/12/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "LogsTableCell.h"

@implementation LogsTableCell

-(void)setData:(NSString*)logs{
    NSArray* timeAndText = [logs componentsSeparatedByString:@"|"];
    if ([timeAndText count] > 1){
        NSString* logTime = [timeAndText objectAtIndex:0];
        NSString* log = [timeAndText objectAtIndex:1];
        _logTime.text = logTime;
        _logLabel.text = log;
        [_logTime setHidden:NO];
        return;
    }
    _logTime.text = @"";
    [_logTime setHidden:YES];
    _logLabel.text = logs;
}

@end
