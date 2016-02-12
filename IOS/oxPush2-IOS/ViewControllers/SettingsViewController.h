//
//  SettingsViewController.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/9/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CustomIOS7AlertView.h"

@interface SettingsViewController : UIViewController <UITableViewDataSource, UITableViewDelegate>{

    NSMutableArray* keyHandleArray;
    IBOutlet UITableView* keyHandleTableView;
    IBOutlet UILabel* keyHandleLabel;
    IBOutlet UIButton* logsButton;
    IBOutlet UIButton* infoButton;
    int rowToDelete;
    
    CustomIOS7AlertView *infoView;
}

@end
