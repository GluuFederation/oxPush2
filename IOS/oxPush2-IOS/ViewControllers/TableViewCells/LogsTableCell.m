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

    _logLabel.text = logs;
}

@end
