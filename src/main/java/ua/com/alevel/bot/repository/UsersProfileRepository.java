package ua.com.alevel.bot.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.bot.model.UserProfileData;


@Repository
public interface UsersProfileRepository extends JpaRepository<UserProfileData, String> {
    UserProfileData findByChatId(long chatId);

    void deleteByChatId(long chatId);
}
