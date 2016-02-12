//
//  LogsViewController.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/12/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "LogsViewController.h"
#import "LogManager.h"
#import "LogsTableCell.h"

@implementation LogsViewController

-(void)viewDidLoad{

    [super viewDidLoad];
    [self getLogs];
}

-(void)getLogs{
    NSString* logs = [[LogManager sharedInstance] getLogs];
    if (logs != nil || ![logs isEqualToString:@""]){
        NSArray* logsAr = [logs componentsSeparatedByString:@"\n"];
        if ([logs length] > 0){
            logsArray = [[NSMutableArray alloc] initWithArray:logsAr];
            [logsTableView reloadData];
            [logsTableView setHidden:NO];
        } else {
            [logsTableView setHidden:YES];
        }
    }else{
        [logsTableView setHidden:YES];
    }
}

#pragma mark UITableview Delegate

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return logsArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    NSString *CellIdentifier= @"LogsTableCellID";
    LogsTableCell *cell = (LogsTableCell*)[tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    [cell setData:[logsArray objectAtIndex:indexPath.row]];
    
    return cell;
}


-(IBAction)back:(id)sender{
    [self.navigationController popViewControllerAnimated:YES];
}

@end
