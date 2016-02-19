//
//  SettingsViewController.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/9/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "SettingsViewController.h"
#import "KeyHandleCell.h"
#import "TokenEntity.h"
#import "DataStoreManager.h"

#define CORNER_RADIUS 5.0

@implementation SettingsViewController

-(void)viewDidLoad{
    [super viewDidLoad];
    [self loadKeyHandlesFromDatabase];
    [self initIfoView];
    
    logsButton.layer.cornerRadius = CORNER_RADIUS;
    infoButton.layer.cornerRadius = CORNER_RADIUS;
    
    UILongPressGestureRecognizer* longPressRecognizer = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(onLongPress:)];
    [longPressRecognizer setMinimumPressDuration:3.0];
    [keyHandleTableView addGestureRecognizer:longPressRecognizer];
    
    keyRenameInfoLabel.text = NSLocalizedString(@"RenameKeyNameInfo", @"Rename Key's Name");
}

-(void)onLongPress:(UILongPressGestureRecognizer*)pGesture
{
    if (pGesture.state == UIGestureRecognizerStateBegan)
    {
        //Do something to tell the user!
        CGPoint touchPoint = [pGesture locationInView:keyHandleTableView];
        NSIndexPath* row = [keyHandleTableView indexPathForRowAtPoint:touchPoint];
        selectedRow = row;
        if (row != nil) {
            //Handle the long press on row
            NSLog(@"Cell title will be changed");
            CustomIOS7AlertView* alertView = [CustomIOS7AlertView alertWithTitle:NSLocalizedString(@"AlertTitle", @"Into") message:NSLocalizedString(@"ChangeKeyHandleName", @"Change KeyHandle Name")];
            [alertView setButtonTitles:[NSArray arrayWithObjects:NSLocalizedString(@"NO", @"NO"), NSLocalizedString(@"YES", @"YES"), nil]];
            [alertView setButtonColors:[NSArray arrayWithObjects:[UIColor redColor], [UIColor greenColor], nil]];//"" = "Do you want to change keyHandle display name?"
            alertView.delegate = self;
            [alertView show];
        }
    }
}

-(void)initIfoView{
    infoView = [CustomIOS7AlertView alertWithTitle:NSLocalizedString(@"AlertTitle", @"Into") message:NSLocalizedString(@"AppName", @"App Name")];
}

-(void)loadKeyHandlesFromDatabase{
    NSArray* keyHandles = [[DataStoreManager sharedInstance] getTokenEntitiesByID:@""];
    keyHandleArray = [[NSMutableArray alloc] initWithArray:keyHandles];
    if ([keyHandleArray count] > 0){
        [keyHandleTableView reloadData];
        [keyHandleTableView setHidden:NO];
    } else {
        [keyHandleTableView setHidden:YES];
    }
    [self initLabel:(int)[keyHandleArray count]];
}

-(void)initLabel:(int)keyCount{
    if (keyCount > 0){
        [keyHandleLabel setText:[NSString stringWithFormat:@"%@ (%i):", NSLocalizedString(@"AvailableKeyHandles", @"Available KeyHandles"), keyCount]];
        [keyRenameInfoLabel setHidden:NO];
    } else {
        [keyHandleLabel setText:[NSString stringWithFormat:@"%@:", NSLocalizedString(@"AvailableKeyHandles", @"Available KeyHandles")]];
        [keyRenameInfoLabel setHidden:YES];
    }
}

-(IBAction)back:(id)sender{
    
    [self.navigationController popToRootViewControllerAnimated:YES];
}

-(IBAction)onInfoClick:(id)sender{
    [infoView show];
}

#pragma mark UITextField Delegate

-(BOOL)textFieldShouldReturn:(UITextField *)textField{
    [textField resignFirstResponder];
    
    KeyHandleCell *cell = (KeyHandleCell*)[keyHandleTableView cellForRowAtIndexPath:selectedRow];
    UITextField* keyTextField = [cell keyHandleTextField];
    [keyTextField setEnabled:NO];
    [[NSUserDefaults standardUserDefaults] setObject:keyTextField.text forKey:@"keyHandleDisplayName"];
    
    return YES;
}

#pragma mark CustomIOS7AlertView Delegate

-(void)customIOS7dialogButtonTouchUpInside:(id)alertView clickedButtonAtIndex:(NSInteger)buttonIndex{
    if (buttonIndex == 1){
        NSLog(@"User wants to change keyHandleName");
        KeyHandleCell *cell = (KeyHandleCell*)[keyHandleTableView cellForRowAtIndexPath:selectedRow];
        UITextField* keyTextField = [cell keyHandleTextField];
        [keyTextField setEnabled:YES];
        [keyTextField becomeFirstResponder];
        [keyTextField setReturnKeyType:UIReturnKeyDone];
        keyTextField.delegate = self;
    }
    [alertView close];
}

#pragma mark UITableview Delegate

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return keyHandleArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    NSString *CellIdentifier= @"KeyHandleCellID";
    KeyHandleCell *cell = (KeyHandleCell*)[tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    TokenEntity* tokenEntity = [keyHandleArray objectAtIndex:indexPath.row];
    [cell setData:tokenEntity];
    
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    KeyHandleCell *cell = (KeyHandleCell*)[tableView cellForRowAtIndexPath:indexPath];
    [[CustomIOS7AlertView alertWithTitle:cell.keyHandleTextField.text message:cell.key] show];
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath{
    rowToDelete = (int)indexPath.row;
    UIAlertController* alert = [UIAlertController alertControllerWithTitle:NSLocalizedString(@"Delete", @"Delete") message:@"Do you want to delete this key?" preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *yesAction = [UIAlertAction
                                   actionWithTitle:NSLocalizedString(@"YES", @"YES action")
                                   style:UIAlertActionStyleDefault
                                   handler:^(UIAlertAction *action)
                                   {
                                       [self deleteRow];
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

-(void)deleteRow{
    [[DataStoreManager sharedInstance] deleteTokenEntitiesByID:@""];
    [keyHandleArray removeObjectAtIndex:0];
    [keyHandleTableView reloadData];
    [self initLabel:(int)[keyHandleArray count]];
}

@end
