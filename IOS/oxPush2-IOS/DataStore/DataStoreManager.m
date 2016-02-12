//
//  DataStoreManager.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/3/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "DataStoreManager.h"
#import "AppDelegate.h"
#import <CoreData/CoreData.h>

#define KEY_ENTITY @"TokenEntity"

@implementation DataStoreManager{

    AppDelegate* appDelegate;
}

+ (instancetype) sharedInstance {
    static id instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

-(void)saveTokenEntity:(TokenEntity*)tokenEntity{
    appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:KEY_ENTITY];
    NSError *error = nil;
    NSArray* eccKeyFetchedArray = [appDelegate.managedObjectContext executeFetchRequest:request error:&error];
    NSManagedObject *newMetaData = [NSEntityDescription insertNewObjectForEntityForName:KEY_ENTITY inManagedObjectContext:appDelegate.managedObjectContext];
    if (eccKeyFetchedArray != nil && [eccKeyFetchedArray count] > 0){
        TokenEntity* eccKeyFetched = [eccKeyFetchedArray objectAtIndex:0];
        if (eccKeyFetched != nil){
            newMetaData = [eccKeyFetchedArray objectAtIndex:0];
        }
    }
    [newMetaData setValue:[tokenEntity ID] forKey:@"id"];
    [newMetaData setValue:[tokenEntity application] forKey:@"application"];
    [newMetaData setValue:[tokenEntity issuer] forKey:@"issuer"];
    [newMetaData setValue:[tokenEntity keyHandle] forKey:@"keyHandle"];
    [newMetaData setValue:[tokenEntity privateKey] forKey:@"privateKey"];
    [newMetaData setValue:[tokenEntity publicKey] forKey:@"publicKey"];
    [newMetaData setValue:[NSNumber numberWithInt:[tokenEntity count]] forKey:@"count"];
    
    error = nil;
    // Save the object to persistent store
    if (![appDelegate.managedObjectContext save:&error]) {
        NSLog(@"Can't Save! %@ %@", error, [error localizedDescription]);
        return;
    }
    NSLog(@"Saved TokenEntity to database success");
}

-(NSArray*)getTokenEntitiesByID:(NSString*)keyID{
    appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    NSMutableArray* entities = [[NSMutableArray alloc] init];
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:KEY_ENTITY];
    NSError *error = nil;
    NSArray* eccKeyFetchedArray = [appDelegate.managedObjectContext executeFetchRequest:request error:&error];
    if (eccKeyFetchedArray != nil && [eccKeyFetchedArray count] > 0){
        for (NSManagedObject *eccKeyFetched in eccKeyFetchedArray){
            if (eccKeyFetched != nil && [eccKeyFetched valueForKey:@"keyHandle"] != nil){
                TokenEntity* tokenEntity = [[TokenEntity alloc] init];
                [tokenEntity setID:[eccKeyFetched valueForKey:@"id"]];
                [tokenEntity setApplication:[eccKeyFetched valueForKey:@"application"]];
                [tokenEntity setIssuer:[eccKeyFetched valueForKey:@"issuer"]];
                [tokenEntity setKeyHandle:[eccKeyFetched valueForKey:@"keyHandle"]];
                [tokenEntity setPrivateKey:[eccKeyFetched valueForKey:@"privateKey"]];
                [tokenEntity setPublicKey:[eccKeyFetched valueForKey:@"publicKey"]];
                NSNumber* count = [eccKeyFetched valueForKey:@"count"];
                [tokenEntity setCount:[count intValue]];
                [entities addObject:tokenEntity];
            }
        }
    }
    return entities;
}

-(int)incrementCountForToken:(TokenEntity*)tokenEntity{
    appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:KEY_ENTITY];
    NSError *error = nil;
    NSArray* eccKeyFetchedArray = [appDelegate.managedObjectContext executeFetchRequest:request error:&error];
    NSManagedObject *newMetaData = [NSEntityDescription insertNewObjectForEntityForName:KEY_ENTITY inManagedObjectContext:appDelegate.managedObjectContext];
    int count = [tokenEntity count] == 0 ? 1 : [tokenEntity count]+1;
    if (eccKeyFetchedArray != nil && [eccKeyFetchedArray count] > 0){
        TokenEntity* eccKeyFetched = [eccKeyFetchedArray objectAtIndex:0];
        if (eccKeyFetched != nil){
            newMetaData = [eccKeyFetchedArray objectAtIndex:0];
            [newMetaData setValue:[NSNumber numberWithInt:count] forKey:@"count"];
        }
    }
    
    error = nil;
    // Save the object to persistent store
    if (![appDelegate.managedObjectContext save:&error]) {
        NSLog(@"Can't Save! %@ %@", error, [error localizedDescription]);
    }
    NSLog(@"Saved TokenEntity count to database success");
    return count;
}

-(BOOL)deleteTokenEntitiesByID:(NSString*)keyID{
    appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
//    NSMutableArray* entities = [[NSMutableArray alloc] init];
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:KEY_ENTITY];
    NSError *error = nil;
    NSArray* eccKeyFetchedArray = [appDelegate.managedObjectContext executeFetchRequest:request error:&error];
    if (eccKeyFetchedArray != nil && [eccKeyFetchedArray count] > 0){
        for (NSManagedObject *eccKeyFetched in eccKeyFetchedArray){
            if (eccKeyFetched != nil){// && [[eccKeyFetched valueForKey:@"id"] isEqualToString:keyID]){
                [appDelegate.managedObjectContext deleteObject:eccKeyFetched];
            }
        }
        return YES;
    }
    return NO;
}

@end
