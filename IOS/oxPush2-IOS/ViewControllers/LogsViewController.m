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
            [logsTableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:logsArray.count-1 inSection:0] atScrollPosition:UITableViewScrollPositionBottom animated:YES];
        } else {
            [logsTableView setHidden:YES];
            [cleanLogs setEnabled:NO];
        }
    }else{
        [logsTableView setHidden:YES];
        [cleanLogs setEnabled:NO];
    }
}

#pragma mark CustomIOS7AlertView Delegate

-(void)customIOS7dialogButtonTouchUpInside:(id)alertView clickedButtonAtIndex:(NSInteger)buttonIndex{
    if (buttonIndex == 1){
        [[LogManager sharedInstance] deleteAllLogs];
        [self getLogs];
    }
    [alertView close];
}

-(IBAction)cleanLogs:(id)sender{
    CustomIOS7AlertView *alertView = [CustomIOS7AlertView alertWithTitle:NSLocalizedString(@"AlertTitle", @"Into") message:NSLocalizedString(@"ClearLogs", @"Clear Logs")];
    [alertView setButtonTitles:[NSArray arrayWithObjects:NSLocalizedString(@"NO", @"NO"), NSLocalizedString(@"YES", @"YES"), nil]];
    [alertView setButtonColors:[NSArray arrayWithObjects:[UIColor redColor], [UIColor greenColor], nil]];
    alertView.delegate = self;
    [alertView show];
}

-(void)showAlertViewWithTitle:(NSString*)title andMessage:(NSString*)message{
    UIAlertController* alert = [UIAlertController alertControllerWithTitle:title message:message preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *yesAction = [UIAlertAction
                                   actionWithTitle:NSLocalizedString(@"YES", @"YES action")
                                   style:UIAlertActionStyleCancel
                                   handler:^(UIAlertAction *action)
                                   {
                                       NSLog(@"YES action");
                                   }];
    UIAlertAction *noAction = [UIAlertAction
                                   actionWithTitle:NSLocalizedString(@"NO", @"NO action")
                                   style:UIAlertActionStyleCancel
                                   handler:^(UIAlertAction *action)
                                   {
                                       NSLog(@"NO action");
                                   }];
    [alert addAction:yesAction];
    [alert addAction:noAction];
    [self presentViewController:alert animated:YES completion:nil];
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
