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

@interface MainViewController : UIViewController <QRCodeReaderDelegate>{
    
    IBOutlet UIButton* scanButton;
    IBOutlet UIButton* infoButton;
    IBOutlet UILabel* statusLabel;
    IBOutlet UIView* statusView;
    
    TJSpinner *circularSpinner;

    BOOL isResultFromScan;
    BOOL isStatusViewVisible;
    QRCodeReaderViewController *qrScanerVC;
}

- (IBAction)scanAction:(id)sender;
- (IBAction)infoAction:(id)sender;

@end

