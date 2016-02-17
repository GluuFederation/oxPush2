//
//  SettingsViewController.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/9/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CustomIOS7AlertView.h"

@interface SettingsViewController : UIViewController <UITableViewDataSource, UITableViewDelegate, CustomIOS7AlertViewDelegate, UITextFieldDelegate>{

    NSMutableArray* keyHandleArray;
    IBOutlet UITableView* keyHandleTableView;
    IBOutlet UILabel* keyHandleLabel;
    IBOutlet UIButton* logsButton;
    IBOutlet UIButton* infoButton;
    int rowToDelete;
    NSIndexPath* selectedRow;
    
    CustomIOS7AlertView *infoView;
}

@end
