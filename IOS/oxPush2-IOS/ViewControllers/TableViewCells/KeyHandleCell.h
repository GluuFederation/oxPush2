//
//  KeyHandleCell.h
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/10/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TokenEntity.h"

@interface KeyHandleCell : UITableViewCell

@property (strong, nonatomic) IBOutlet UILabel* keyHandleLabel;

-(void)setData:(TokenEntity*)tokenEntity;

@end
