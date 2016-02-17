//
//  KeyHandleCell.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/10/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "KeyHandleCell.h"
#import "Base64.h"

@implementation KeyHandleCell

-(void)setData:(TokenEntity*)tokenEntity{
    _key = [[tokenEntity keyHandle] base64EncodedString];
    NSString* displayName = [NSString stringWithFormat:@"%@ %@", NSLocalizedString(@"keyHandleFor", @"keyHandleFor"), [[UIDevice currentDevice] name]];
    NSString* keyHandleName = [[NSUserDefaults standardUserDefaults] stringForKey:@"keyHandleDisplayName"];
    displayName = keyHandleName != nil ? keyHandleName : displayName;
    [self.keyHandleTextField setText:displayName];
}

@end
