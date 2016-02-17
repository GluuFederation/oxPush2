//
//  MainViewController.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/1/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QRCodeReaderDelegate.h"
#import "TJSpinner.h"
#import "UserLoginInfo.h"

@interface MainViewController : UIViewController <QRCodeReaderDelegate>{

    IBOutlet UILabel* titleLabel;
    
    IBOutlet UIButton* scanButton;
    IBOutlet UIButton* infoButton;
    IBOutlet UILabel* statusLabel;
    IBOutlet UIView* statusView;
    
    IBOutlet UIView* userInfoView;
    IBOutlet UILabel* userNameLabel;
    IBOutlet UILabel* userApplicationLabel;
    IBOutlet UILabel* userIssuerLabel;
    IBOutlet UILabel* userCreatedLabel;
    IBOutlet UILabel* userAuthencicationModeLabel;
    IBOutlet UILabel* userAuthencicationTypeLabel;
    
    TJSpinner *circularSpinner;

    BOOL isResultFromScan;
    BOOL isStatusViewVisible;
    BOOL isUserInfo;
    QRCodeReaderViewController *qrScanerVC;

}

- (IBAction)scanAction:(id)sender;
- (IBAction)infoAction:(id)sender;

@end

