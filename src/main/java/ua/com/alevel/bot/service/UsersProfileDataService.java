package ua.com.alevel.bot.service;

import org.springframework.stereotype.Service;
import ua.com.alevel.bot.model.UserProfileData;
import ua.com.alevel.bot.repository.UsersProfileRepository;

import java.util.List;

@Service
public class UsersProfileDataService {

    private final UsersProfileRepository profileRepository;

    public UsersProfileDataService(UsersProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public List<UserProfileData> getAllProfiles() {
        return profileRepository.findAll();
    }

    public void saveUserProfileData(UserProfileData userProfileData) {
        profileRepository.save(userProfileData);
    }

    public void deleteUsersProfileData(String profileDataId) {
        profileRepository.deleteById(profileDataId);
    }

    public UserProfileData getUserProfileData(long chatId) {
        return profileRepository.findByChatId(chatId);
    }


}
