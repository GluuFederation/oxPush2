//
//  DataStoreManager.m
//  oxPush2-IOS
//
//  Created by Nazar Yavornytskyy on 2/3/16.
//  Copyright © 2016 Nazar Yavornytskyy. All rights reserved.
//

#import "DataStoreManager.h"
#import "DataStoreDelegate.h"
#import <CoreData/CoreData.h>
#import "UserLoginInfo.h"

#define KEY_ENTITY @"TokenEntity"
#define USER_INFO_ENTITY @"LoginInfoEntity"

#define USER_NAME_KEY @"userName"
#define APPLICATION_KEY @"application"
#define ISSUER_KEY @"issuer"
#define CREATED_KEY @"created"
#define AUTHENTICATION_MODE @"authenticationMode"
#define AUTHENTICATION_TYPE @"authenticationType"
#define LOCATION_IP @"locationIP"
#define LOCATION_CITY @"locationCity"
#define LOG_STATE @"logState"
#define ERROR_MESSAGE @"errorMessage"

#define ID_KEY @"id"
#define KEYHANDLE_KEY @"keyHandle"
#define PRIVATE_KEY @"privateKey"
#define PUBLIC_KEY @"publicKey"
#define COUNT_KEY @"count"
#define KEY_NAME @"displayName"


@implementation DataStoreManager{

}

static DataStoreDelegate* appDelegate;

+ (instancetype) sharedInstance {
    static id instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
        appDelegate = [[DataStoreDelegate alloc] init];
    });
    return instance;
}

-(void)saveTokenEntity:(TokenEntity*)tokenEntity{
//    appDelegate = (DataStoreDelegate *)[[UIApplication sharedApplication] delegate];
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:KEY_ENTITY];
    NSError *error = nil;
    NSArray* eccKeyFetchedArray = [appDelegate.managedObjectContext executeFetchRequest:request error:&error];
    NSManagedObject *newMetaData = [NSEntityDescription insertNewObjectForEntityForName:KEY_ENTITY inManagedObjectContext:appDelegate.managedObjectContext];
    if (eccKeyFetchedArray != nil && [eccKeyFetchedArray count] > 0){
        TokenEntity* eccKeyFetched = [eccKeyFetchedArray objectAtIndex:0];
        if (eccKeyFetched != nil){
//            NSString* tokenID = [NSString stringWithFormat:@"%@%@", [eccKeyFetched valueForKey:ISSUER_KEY], [eccKeyFetched valueForKey:APPLICATION_KEY]];
            NSString* tokenID = [NSString stringWithFormat:@"%@", [eccKeyFetched valueForKey:APPLICATION_KEY]];
            NSString* tokenEntityID = [NSString stringWithFormat:@"%@", [tokenEntity application]];
            if ([tokenID isEqualToString:tokenEntityID]){
                newMetaData = [eccKeyFetchedArray objectAtIndex:0];
            }
        }
    }
    [newMetaData setValue:[tokenEntity ID] forKey:ID_KEY];
    [newMetaData setValue:[tokenEntity application] forKey:APPLICATION_KEY];
    [newMetaData setValue:[tokenEntity issuer] forKey:ISSUER_KEY];
    [newMetaData setValue:[tokenEntity keyHandle] forKey:KEYHANDLE_KEY];
    [newMetaData setValue:[tokenEntity privateKey] forKey:PRIVATE_KEY];
    [newMetaData setValue:[tokenEntity publicKey] forKey:PUBLIC_KEY];
    [newMetaData setValue:[tokenEntity userName] forKey:USER_NAME_KEY];
    [newMetaData setValue:[tokenEntity pairingTime] forKey:CREATED_KEY];
    [newMetaData setValue:[tokenEntity authenticationMode] forKey:AUTHENTICATION_MODE];
    [newMetaData setValue:[tokenEntity authenticationType] forKey:AUTHENTICATION_TYPE];
    [newMetaData setValue:[NSNumber numberWithInt:[tokenEntity count]] forKey:COUNT_KEY];
    NSString* application = [tokenEntity application];
    NSURL* urlApplication = [NSURL URLWithString:application];
    NSString* displayName = [NSString stringWithFormat:@"%@ %@", NSLocalizedString(@"keyHandleFor", @"keyHandleFor"), [urlApplication host]];
    [newMetaData setValue:displayName forKey:KEY_NAME];
    
    error = nil;
    // Save the object to persistent store
    if (![appDelegate.managedObjectContext save:&error]) {
        NSLog(@"Can't Save! %@ %@", error, [error localizedDescription]);
        return;
    }
    NSLog(@"Saved TokenEntity to database success");
}

-(NSArray*)getTokenEntitiesByID:(NSString*)keyID{
//    appDelegate = (DataStoreDelegate *)[[UIApplication sharedApplication] delegate];
    NSMutableArray* entities = [[NSMutableArray alloc] init];
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:KEY_ENTITY];
    NSError *error = nil;
    NSArray* eccKeyFetchedArray = [appDelegate.managedObjectContext executeFetchRequest:request error:&error];
    if (eccKeyFetchedArray != nil && [eccKeyFetchedArray count] > 0){
        for (NSManagedObject *eccKeyFetched in eccKeyFetchedArray){
            if (eccKeyFetched != nil && [eccKeyFetched valueForKey:KEYHANDLE_KEY] != nil){
                NSString* tokenID = [NSString stringWithFormat:@"%@", [eccKeyFetched valueForKey:APPLICATION_KEY]];
                if ([tokenID isEqualToString:keyID]){
                    TokenEntity* tokenEntity = [[TokenEntity alloc] init];
                    [tokenEntity setID:[eccKeyFetched valueForKey:ID_KEY]];
                    [tokenEntity setApplication:[eccKeyFetched valueForKey:APPLICATION_KEY]];
                    [tokenEntity setIssuer:[eccKeyFetched valueForKey:ISSUER_KEY]];
                    [tokenEntity setKeyHandle:[eccKeyFetched valueForKey:KEYHANDLE_KEY]];
                    [tokenEntity setPrivateKey:[eccKeyFetched valueForKey:PRIVATE_KEY]];
                    [tokenEntity setPublicKey:[eccKeyFetched valueForKey:PUBLIC_KEY]];
                    [tokenEntity setUserName:[eccKeyFetched valueForKey:USER_NAME_KEY]];
                    [tokenEntity setPairingTime:[eccKeyFetched valueForKey:CREATED_KEY]];
                    [tokenEntity setAuthenticationMode:[eccKeyFetched valueForKey:AUTHENTICATION_MODE]];
                    [tokenEntity setAuthenticationType:[eccKeyFetched valueForKey:AUTHENTICATION_TYPE]];
                    NSNumber* count = [eccKeyFetched valueForKey:COUNT_KEY];
                    [tokenEntity setCount:[count intValue]];
                    [tokenEntity setKeyName:[eccKeyFetched valueForKey:KEY_NAME]];
                    [entities addObject:tokenEntity];
                }
            }
        }
    }
    return entities;
}

-(void)setTokenEntitiesNameByID:(NSString*)keyID newName:(NSString*)newName{
//    appDelegate = (DataStoreDelegate *)[[UIApplication sharedApplication] delegate];
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:KEY_ENTITY];
    NSError *error = nil;
    NSArray* eccKeyFetchedArray = [appDelegate.managedObjectContext executeFetchRequest:request error:&error];
    if (eccKeyFetchedArray != nil && [eccKeyFetchedArray count] > 0){
        for (NSManagedObject *eccKeyFetched in eccKeyFetchedArray){
            if (eccKeyFetched != nil && [eccKeyFetched valueForKey:KEYHANDLE_KEY] != nil){
                NSString* tokenID = [NSString stringWithFormat:@"%@", [eccKeyFetched valueForKey:APPLICATION_KEY]];
                if ([tokenID isEqualToString:keyID]){
                    [eccKeyFetched setValue:newName forKey:KEY_NAME];
                }
            }
        }
    }
    error = nil;
    // Save the object to persistent store
    if (![appDelegate.managedObjectContext save:&error]) {
        NSLog(@"Can't Save! %@ %@", error, [error localizedDescription]);
    }
    NSLog(@"Saved new TokenEntity name in database success");
}

-(NSArray*)getTokenEntities{
//    appDelegate = (DataStoreDelegate *)[[UIApplication sharedApplication] delegate];
    NSMutableArray* entities = [[NSMutableArray alloc] init];
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:KEY_ENTITY];
    NSError *error = nil;
    NSArray* eccKeyFetchedArray = [appDelegate.managedObjectContext executeFetchRequest:request error:&error];
    if (eccKeyFetchedArray != nil && [eccKeyFetchedArray count] > 0){
        for (NSManagedObject *eccKeyFetched in eccKeyFetchedArray){
            if (eccKeyFetched != nil && [eccKeyFetched valueForKey:KEYHANDLE_KEY] != nil){
                TokenEntity* tokenEntity = [[TokenEntity alloc] init];
                [tokenEntity setID:[eccKeyFetched valueForKey:ID_KEY]];
                [tokenEntity setApplication:[eccKeyFetched valueForKey:APPLICATION_KEY]];
                [tokenEntity setIssuer:[eccKeyFetched valueForKey:ISSUER_KEY]];
                [tokenEntity setKeyHandle:[eccKeyFetched valueForKey:KEYHANDLE_KEY]];
                [tokenEntity setPrivateKey:[eccKeyFetched valueForKey:PRIVATE_KEY]];
                [tokenEntity setPublicKey:[eccKeyFetched valueForKey:PUBLIC_KEY]];
                [tokenEntity setUserName:[eccKeyFetched valueForKey:USER_NAME_KEY]];
                [tokenEntity setPairingTime:[eccKeyFetched valueForKey:CREATED_KEY]];
                [tokenEntity setAuthenticationMode:[eccKeyFetched valueForKey:AUTHENTICATION_MODE]];
                [tokenEntity setAuthenticationType:[eccKeyFetched valueForKey:AUTHENTICATION_TYPE]];
                NSNumber* count = [eccKeyFetched valueForKey:COUNT_KEY];
                [tokenEntity setCount:[count intValue]];
                [tokenEntity setKeyName:[eccKeyFetched valueForKey:KEY_NAME]];
                [entities addObject:tokenEntity];
            }
        }
    }
    return entities;
}

-(TokenEntity*)getTokenEntityByKeyHandle:(NSString*)keyHandle{
//    appDelegate = (DataStoreDelegate *)[[UIApplication sharedApplication] delegate];
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:KEY_ENTITY];
    NSError *error = nil;
    NSArray* eccKeyFetchedArray = [appDelegate.managedObjectContext executeFetchRequest:request error:&error];
    if (eccKeyFetchedArray != nil && [eccKeyFetchedArray count] > 0){
        for (NSManagedObject *eccKeyFetched in eccKeyFetchedArray){
            if (eccKeyFetched != nil && [eccKeyFetched valueForKey:KEYHANDLE_KEY] != nil && [keyHandle isEqualToString:[eccKeyFetched valueForKey:KEYHANDLE_KEY]]){
                TokenEntity* tokenEntity = [[TokenEntity alloc] init];
                [tokenEntity setID:[eccKeyFetched valueForKey:ID_KEY]];
                [tokenEntity setApplication:[eccKeyFetched valueForKey:APPLICATION_KEY]];
                [tokenEntity setIssuer:[eccKeyFetched valueForKey:ISSUER_KEY]];
                [tokenEntity setKeyHandle:[eccKeyFetched valueForKey:KEYHANDLE_KEY]];
                [tokenEntity setPrivateKey:[eccKeyFetched valueForKey:PRIVATE_KEY]];
                [tokenEntity setPublicKey:[eccKeyFetched valueForKey:PUBLIC_KEY]];
                NSNumber* count = [eccKeyFetched valueForKey:COUNT_KEY];
                [tokenEntity setCount:[count intValue]];
                return tokenEntity;
            }
        }
    }
    return nil;
}

-(int)incrementCountForToken:(TokenEntity*)tokenEntity{
//    appDelegate = (DataStoreDelegate *)[[UIApplication sharedApplication] delegate];
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:KEY_ENTITY];
    NSError *error = nil;
    NSArray* eccKeyFetchedArray = [appDelegate.managedObjectContext executeFetchRequest:request error:&error];
    NSManagedObject *newMetaData = [NSEntityDescription insertNewObjectForEntityForName:KEY_ENTITY inManagedObjectContext:appDelegate.managedObjectContext];
    int count = [tokenEntity count]+1;//[tokenEntity count] == 0 ? 1 : 
    if (eccKeyFetchedArray != nil && [eccKeyFetchedArray count] > 0){
        for (NSManagedObject* eccKeyObject in eccKeyFetchedArray){
            TokenEntity* eccKeyFetched = (TokenEntity*)eccKeyObject;
            if (eccKeyFetched != nil){
                NSString* tokenID = [NSString stringWithFormat:@"%@", [eccKeyFetched valueForKey:APPLICATION_KEY]];
                NSString* tokenEntityID = [NSString stringWithFormat:@"%@", [tokenEntity application]];
                if ([tokenID isEqualToString:tokenEntityID]){
                    newMetaData = eccKeyObject;
                    [newMetaData setValue:[NSNumber numberWithInt:count] forKey:COUNT_KEY];
                }
            }
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
//    appDelegate = (DataStoreDelegate *)[[UIApplication sharedApplication] delegate];
//    NSMutableArray* entities = [[NSMutableArray alloc] init];
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:KEY_ENTITY];
    NSError *error = nil;
    NSArray* eccKeyFetchedArray = [appDelegate.managedObjectContext executeFetchRequest:request error:&error];
    if (eccKeyFetchedArray != nil && [eccKeyFetchedArray count] > 0){
        for (NSManagedObject *eccKeyFetched in eccKeyFetchedArray){
            if (eccKeyFetched != nil){// && [[eccKeyFetched valueForKey:@"id"] isEqualToString:keyID]){
                NSString* tokenID = [NSString stringWithFormat:@"%@", [eccKeyFetched valueForKey:APPLICATION_KEY]];
                if ([tokenID isEqualToString:keyID]){
                    [appDelegate.managedObjectContext deleteObject:eccKeyFetched];
                    if (![appDelegate.managedObjectContext save:&error]) {
                        NSLog(@"Couldn't save: %@", error);
                    }
                }
            }
        }
        return YES;
    }
    return NO;
}

-(void)saveUserLoginInfo:(UserLoginInfo*)userLoginInfo{
//    appDelegate = (DataStoreDelegate *)[[UIApplication sharedApplication] delegate];
    NSError *error = nil;
    NSManagedObject *newMetaData = [NSEntityDescription insertNewObjectForEntityForName:USER_INFO_ENTITY inManagedObjectContext:appDelegate.managedObjectContext];
    [newMetaData setValue:[userLoginInfo userName] forKey:USER_NAME_KEY];
    [newMetaData setValue:[userLoginInfo application] forKey:APPLICATION_KEY];
    [newMetaData setValue:[userLoginInfo issuer] forKey:ISSUER_KEY];
    [newMetaData setValue:[userLoginInfo created] forKey:CREATED_KEY];
    [newMetaData setValue:[userLoginInfo authenticationMode] forKey:AUTHENTICATION_MODE];
    [newMetaData setValue:[userLoginInfo authenticationType] forKey:AUTHENTICATION_TYPE];
    [newMetaData setValue:[userLoginInfo locationIP] forKey:LOCATION_IP];
    [newMetaData setValue:[userLoginInfo locationCity] forKey:LOCATION_CITY];
    switch ([userLoginInfo logState]) {
        case LOGIN_SUCCESS:
            [newMetaData setValue:[NSNumber numberWithInt:0] forKey:LOG_STATE];
            break;
        case LOGIN_FAILED:
            [newMetaData setValue:[NSNumber numberWithInt:1] forKey:LOG_STATE];
            break;
        case LOGIN_DECLINED:
            [newMetaData setValue:[NSNumber numberWithInt:2] forKey:LOG_STATE];
            break;
        case ENROLL_SUCCESS:
            [newMetaData setValue:[NSNumber numberWithInt:3] forKey:LOG_STATE];
            break;
        case ENROLL_FAILED:
            [newMetaData setValue:[NSNumber numberWithInt:4] forKey:LOG_STATE];
            break;
        case ENROLL_DECLINED:
            [newMetaData setValue:[NSNumber numberWithInt:5] forKey:LOG_STATE];
            break;
        case UNKNOWN_ERROR:
            [newMetaData setValue:[NSNumber numberWithInt:6] forKey:LOG_STATE];
            break;
            
        default:
            break;
    }
    [newMetaData setValue:[userLoginInfo errorMessage] forKey:ERROR_MESSAGE];
    
    error = nil;
    // Save the object to persistent store
    if (![appDelegate.managedObjectContext save:&error]) {
        NSLog(@"Can't Save! %@ %@", error, [error localizedDescription]);
        return;
    }
    NSLog(@"Saved UserLoginInfoEntity to database success");
}

-(NSArray*)getUserLoginInfo{
//    appDelegate = (DataStoreDelegate *)[[UIApplication sharedApplication] delegate];
    NSMutableArray* entities = [[NSMutableArray alloc] init];
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:USER_INFO_ENTITY];
    NSError *error = nil;
    NSArray* eccKeyFetchedArray = [appDelegate.managedObjectContext executeFetchRequest:request error:&error];
    if (eccKeyFetchedArray != nil && [eccKeyFetchedArray count] > 0){
        for (NSManagedObject *eccKeyFetched in eccKeyFetchedArray){
            if (eccKeyFetched != nil){
                UserLoginInfo* userInfoEntity = [[UserLoginInfo alloc] init];
                [userInfoEntity setCreated:[eccKeyFetched valueForKey:CREATED_KEY]];
                [userInfoEntity setApplication:[eccKeyFetched valueForKey:APPLICATION_KEY]];
                [userInfoEntity setIssuer:[eccKeyFetched valueForKey:ISSUER_KEY]];
                [userInfoEntity setUserName:[eccKeyFetched valueForKey:USER_NAME_KEY]];
                [userInfoEntity setAuthenticationMode:[eccKeyFetched valueForKey:AUTHENTICATION_MODE]];
                [userInfoEntity setAuthenticationType:[eccKeyFetched valueForKey:AUTHENTICATION_TYPE]];
                [userInfoEntity setLocationIP:[eccKeyFetched valueForKey:LOCATION_IP]];
                [userInfoEntity setLocationCity:[eccKeyFetched valueForKey:LOCATION_CITY]];
                [userInfoEntity setErrorMessage:[eccKeyFetched valueForKey:ERROR_MESSAGE]];
                NSInteger state = [[eccKeyFetched valueForKey:LOG_STATE] integerValue];
                switch (state) {
                    case 0:
                        [userInfoEntity setLogState:LOGIN_SUCCESS];
                        break;
                    case 1:
                        [userInfoEntity setLogState:LOGIN_FAILED];
                        break;
                    case 2:
                        [userInfoEntity setLogState:LOGIN_DECLINED];
                        break;
                    case 3:
                        [userInfoEntity setLogState:ENROLL_SUCCESS];
                        break;
                    case 4:
                        [userInfoEntity setLogState:ENROLL_FAILED];
                        break;
                    case 5:
                        [userInfoEntity setLogState:ENROLL_DECLINED];
                        break;
                    case 6:
                        [userInfoEntity setLogState:UNKNOWN_ERROR];
                        break;
                        
                    default:
                        break;
                }
                [entities addObject:userInfoEntity];
            }
        }
    }
    return entities;
}

-(BOOL)deleteAllLogs{
//    appDelegate = (DataStoreDelegate *)[[UIApplication sharedApplication] delegate];
    //    NSMutableArray* entities = [[NSMutableArray alloc] init];
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:USER_INFO_ENTITY];
    NSError *error = nil;
    NSArray* eccKeyFetchedArray = [appDelegate.managedObjectContext executeFetchRequest:request error:&error];
    if (eccKeyFetchedArray != nil && [eccKeyFetchedArray count] > 0){
        for (NSManagedObject *eccKeyFetched in eccKeyFetchedArray){
            if (eccKeyFetched != nil){
                [appDelegate.managedObjectContext deleteObject:eccKeyFetched];
                if (![appDelegate.managedObjectContext save:&error]) {
                    NSLog(@"Couldn't save: %@", error);
                }
            }
        }
        return YES;
    }
    return NO;
}

@end
