//
//  ViewController.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/1/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "MainViewController.h"
#import "QRCodeReader.h"
#import "QRCodeReaderViewController.h"
#import "Constants.h"
#import "OXPushManager.h"
#import "LogManager.h"

#define CORNER_RADIUS 8.0

NSString *const kTJCircularSpinner = @"TJCircularSpinner";

@interface MainViewController ()

@end

@implementation MainViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initWiget];
    [self initNotifications];
    [self initQRScanner];
    [self adoptViewForDevice];
}

-(void)initWiget{
    scanButton.layer.cornerRadius = 5.0;
    statusView.layer.cornerRadius = 5.0;
    
    circularSpinner = [[TJSpinner alloc] initWithSpinnerType:kTJCircularSpinner];
    [circularSpinner setFrame:CGRectMake(scanButton.frame.origin.x + 50, scanButton.frame.origin.y - 100, 50, 50)];
    circularSpinner.hidesWhenStopped = YES;
    circularSpinner.radius = 10;
    circularSpinner.pathColor = [UIColor whiteColor];
    circularSpinner.fillColor = [UIColor greenColor];
    circularSpinner.thickness = 7;
    [circularSpinner setHidden:YES];
    [self.view addSubview:circularSpinner];
}

-(void)adoptViewForDevice{
    CGPoint center = CGPointMake(self.view.center.x, self.view.center.y);
    [scanButton setCenter:center];
}

-(void)initNotifications{
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notificationRecieved:) name:NOTIFICATION_REGISTRATION_SUCCESS object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notificationRecieved:) name:NOTIFICATION_REGISTRATION_FAILED object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notificationRecieved:) name:NOTIFICATION_AUTENTIFICATION_SUCCESS object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notificationRecieved:) name:NOTIFICATION_AUTENTIFICATION_FAILED object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notificationRecieved:) name:NOTIFICATION_REGISTRATION_STARTING object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notificationRecieved:) name:NOTIFICATION_AUTENTIFICATION_STARTING object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notificationRecieved:) name:NOTIFICATION_ERROR object:nil];

}

-(void)notificationRecieved:(NSNotification*)notification{
    NSString* step = [notification.userInfo valueForKey:@"oneStep"];
    BOOL oneStep = [step boolValue];
    NSString* message = @"";
    if ([[notification name] isEqualToString:NOTIFICATION_REGISTRATION_SUCCESS]){
        [circularSpinner setHidden:YES];
        message = NSLocalizedString(@"SuccessEnrollment", @"Success Authentication");
        if (oneStep){
            message = [NSString stringWithFormat:@"%@%@", NSLocalizedString(@"OneStep", @"OneStep Authentication"), NSLocalizedString(@"SuccessEnrollment", @"Success Authentication")];
        } else {
            message = [NSString stringWithFormat:@"%@%@", NSLocalizedString(@"TwoStep", @"TwoStep Authentication"), NSLocalizedString(@"SuccessEnrollment", @"Success Authentication")];
        }
        [self showAlertViewWithTitle:NSLocalizedString(@"AlertTitleSuccess", @"Success") andMessage:message];
    } else
    if ([[notification name] isEqualToString:NOTIFICATION_REGISTRATION_FAILED]){
        [circularSpinner setHidden:YES];
        message = NSLocalizedString(@"FailedEnrollment", @"Failed Authentication");
        if (oneStep){
            message = [NSString stringWithFormat:@"%@%@", NSLocalizedString(@"OneStep", @"OneStep Authentication"), NSLocalizedString(@"FailedEnrollment", @"Failed Authentication")];
        } else {
            message = [NSString stringWithFormat:@"%@%@", NSLocalizedString(@"TwoStep", @"TwoStep Authentication"), NSLocalizedString(@"FailedEnrollment", @"Failed Authentication")];
        }
    } else
    if ([[notification name] isEqualToString:NOTIFICATION_REGISTRATION_STARTING]){
        message = NSLocalizedString(@"StartRegistration", @"Registration...");
        if (oneStep){
            message = [NSString stringWithFormat:@"%@%@", NSLocalizedString(@"OneStep", @"OneStep Authentication"), NSLocalizedString(@"StartRegistration", @"Registration...")];
        } else {
            message = [NSString stringWithFormat:@"%@%@", NSLocalizedString(@"TwoStep", @"TwoStep Authentication"), NSLocalizedString(@"StartRegistration", @"Registration...")];
        }
    } else
    if ([[notification name] isEqualToString:NOTIFICATION_AUTENTIFICATION_SUCCESS]){
        [circularSpinner setHidden:YES];
        message = NSLocalizedString(@"SuccessEnrollment", @"Success Authentication");
        if (oneStep){
            message = [NSString stringWithFormat:@"%@%@", NSLocalizedString(@"OneStep", @"OneStep Authentication"), NSLocalizedString(@"SuccessEnrollment", @"Success Authentication")];
        } else {
            message = [NSString stringWithFormat:@"%@%@", NSLocalizedString(@"TwoStep", @"TwoStep Authentication"), NSLocalizedString(@"SuccessEnrollment", @"Success Authentication")];
        }
        [self showAlertViewWithTitle:NSLocalizedString(@"AlertTitleSuccess", @"Success") andMessage:message];
    } else
    if ([[notification name] isEqualToString:NOTIFICATION_AUTENTIFICATION_FAILED]){
        [circularSpinner setHidden:YES];
        message = NSLocalizedString(@"FailedEnrollment", @"Failed Authentication");
        if (oneStep){
            message = [NSString stringWithFormat:@"%@%@", NSLocalizedString(@"OneStep", @"OneStep Authentication"), NSLocalizedString(@"FailedEnrollment", @"Failed Authentication")];
        } else {
            message = [NSString stringWithFormat:@"%@%@", NSLocalizedString(@"TwoStep", @"TwoStep Authentication"), NSLocalizedString(@"FailedEnrollment", @"Failed Authentication")];
        }
    } else
    if ([[notification name] isEqualToString:NOTIFICATION_AUTENTIFICATION_STARTING]){
        message = NSLocalizedString(@"StartAuthentication", @"Authentication...");
        if (oneStep){
            message = [NSString stringWithFormat:@"%@%@", NSLocalizedString(@"OneStep", @"OneStep Authentication"), NSLocalizedString(@"StartAuthentication", @"Authentication...")];
        } else {
            message = [NSString stringWithFormat:@"%@%@", NSLocalizedString(@"TwoStep", @"TwoStep Authentication"), NSLocalizedString(@"StartAuthentication", @"Authentication...")];
        }
    } else
    if ([[notification name] isEqualToString:NOTIFICATION_ERROR]){
        [circularSpinner setHidden:YES];
        message = [notification object];
    } else 
    if ([[notification name] isEqualToString:NOTIFICATION_UNSUPPORTED_VERSION]){
        [circularSpinner setHidden:YES];
        message = NSLocalizedString(@"UnsupportedU2FV2Version", @"Unsupported U2F_V2 version...");
    }
    [self updateStatus:message];
    [self performSelector:@selector(hideStatusBar) withObject:nil afterDelay:5.0];
}

-(void)initQRScanner{
    // Create the reader object
    QRCodeReader *reader = [QRCodeReader readerWithMetadataObjectTypes:@[AVMetadataObjectTypeQRCode]];
    
    // Instantiate the view controller
    qrScanerVC = [QRCodeReaderViewController readerWithCancelButtonTitle:NSLocalizedString(@"Cancel", @"Cancel") codeReader:reader startScanningAtLoad:YES showSwitchCameraButton:YES showTorchButton:YES];
    
    // Set the presentation style
    qrScanerVC.modalPresentationStyle = UIModalPresentationFormSheet;
    
    // Define the delegate receiver
    qrScanerVC.delegate = self;
    
    // Or use blocks
    [reader setCompletionWithBlock:^(NSString *resultAsString) {
        if(resultAsString && !isResultFromScan){
            isResultFromScan = YES;
            NSLog(@"%@", resultAsString);
            [self sendQRCodeRequest:resultAsString];
            [qrScanerVC dismissViewControllerAnimated:YES completion:nil];
            [circularSpinner setHidden:YES];
        }
    }];
    
    isResultFromScan = NO;
}

-(void)sendQRCodeRequest:(NSString*)jsonAsString{
    NSData *data = [jsonAsString dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary* jsonDictionary = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    if (jsonDictionary != nil){
        OXPushManager* oxPushManager = [[OXPushManager alloc] init];
        [oxPushManager onOxPushApproveRequest:jsonDictionary];
    } else {
//        [self showAlertViewWithTitle:NSLocalizedString(@"AlertTitle", @"Info") andMessage:NSLocalizedString(@"WrongQRImage", @"Wrong QR Code image")];
//        [circularSpinner setHidden:YES];
        [self updateStatus:NSLocalizedString(@"WrongQRImage", @"Wrong QR Code image")];
        [self performSelector:@selector(hideStatusBar) withObject:nil afterDelay:5.0];
    }
}

#pragma mark - Action Methods

- (IBAction)scanAction:(id)sender
{
    [self initQRScanner];
    if ([QRCodeReader isAvailable]){
        [circularSpinner setHidden:NO];
        [circularSpinner startAnimating];
        [self updateStatus:NSLocalizedString(@"QRCodeScanning", @"QR Code Scanning")];
        [self presentViewController:qrScanerVC animated:YES completion:NULL];
    } else {
        [self showAlertViewWithTitle:NSLocalizedString(@"AlertTitle", @"Info") andMessage:NSLocalizedString(@"AlertMessageNoQRScanning", @"No QR Scanning available")];
    }
}

-(void)showAlertViewWithTitle:(NSString*)title andMessage:(NSString*)message{
    UIAlertController* alert = [UIAlertController alertControllerWithTitle:title message:message preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *cancelAction = [UIAlertAction
                                   actionWithTitle:NSLocalizedString(@"OK", @"OK action")
                                   style:UIAlertActionStyleCancel
                                   handler:^(UIAlertAction *action)
                                   {
                                       NSLog(@"Cancel action");
                                   }];
    [alert addAction:cancelAction];
    [self presentViewController:alert animated:YES completion:nil];
}

#pragma mark - QRCodeReader Delegate Methods

- (void)reader:(QRCodeReaderViewController *)reader didScanResult:(NSString *)result
{
    [self dismissViewControllerAnimated:YES completion:^{
        NSLog(@"%@", result);
    }];
}

- (void)readerDidCancel:(QRCodeReaderViewController *)reader
{
    [self dismissViewControllerAnimated:YES completion:NULL];
    if (!isResultFromScan){
        [self updateStatus:NSLocalizedString(@"QRCodeCalceled", @"QR Code Calceled")];
        [circularSpinner setHidden:YES];
        [self performSelector:@selector(hideStatusBar) withObject:nil afterDelay:5.0];
    }
}

- (IBAction)infoAction:(id)sender{
    if (!isStatusViewVisible){
        [self updateStatus:nil];
        [self performSelector:@selector(hideStatusBar) withObject:nil afterDelay:5.0];
    }
}

-(void)updateStatus:(NSString*)status{
    if (status != nil){
        statusLabel.text = status;
        [[LogManager sharedInstance] addLog:status];
    }
    [UIView animateWithDuration:0.2 animations:^{
        [statusView setAlpha:0.0];
        [statusView setCenter:CGPointMake(statusView.center.x, 0)];
    } completion:^(BOOL finished) {
        
        [UIView animateWithDuration:0.5 animations:^{
            [statusView setAlpha:1.0];
            [statusView setCenter:CGPointMake(statusView.center.x, 85)];
            isStatusViewVisible = YES;
        }];
        
    }];
}

-(void)hideStatusBar{
    [UIView animateWithDuration:1.0 animations:^{
        [statusView setAlpha:0.0];
        isStatusViewVisible = NO;
    } completion:^(BOOL finished) {
        //
    }];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
