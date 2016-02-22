//
//  LogsViewController.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/12/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CustomIOS7AlertView.h"

@interface LogsViewController : UIViewController <UITableViewDataSource, UITableViewDelegate,CustomIOS7AlertViewDelegate>{

    NSMutableArray* logsArray;
    IBOutlet UITableView* logsTableView;
    IBOutlet UIButton* cleanLogs;
}

@end
