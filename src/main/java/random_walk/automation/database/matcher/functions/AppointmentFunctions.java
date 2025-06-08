package random_walk.automation.database.matcher.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.database.matcher.entities.Appointment;
import random_walk.automation.database.matcher.repos.AppointmentRepository;
import ru.testit.annotations.Description;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentFunctions {

    private final AppointmentRepository appointmentRepository;

    @Step
    @Title("AND: Удаляем существующую встречу из базы данных")
    @Description("Удаляем запись из таблицы appointment по appointment_id = {appointmentId}")
    public void deleteByAppointmentId(UUID appointmentId) {
        appointmentRepository.deleteByAppointmentId(appointmentId);
    }

    @Step
    @Title("AND: Получаем участников существующей встречи из базы данных")
    @Description("Для записи в таблицы appointment с appointment_id = {appointment_id} получаем список участников")
    public List<UUID> getAppointmentParticipants(UUID appointmentId) {
        return appointmentRepository.findByAppointmentId(appointmentId).stream().map(Appointment::getPersonId).toList();
    }

    @Step
    @Title("AND: Получаем все встречи пользователя из базы данных")
    @Description("Записи из таблицы appointment для пользователя {personId}")
    public List<Appointment> getAppointmentsByPersonId(UUID personId) {
        return appointmentRepository.findAllByPersonId(personId);
    }

    @Step
    @Title("AND: Получаем информацию о назначенной встрече между пользователями из базы данных")
    @Description("Запись из таблицы appointment для пользователей {firstUser} и {secondUser}")
    public List<UUID> getUsersAppointment(UUID firstUser, UUID secondUser) {
        var firstUserAppointments = getAppointmentsByPersonId(firstUser).stream().map(Appointment::getAppointmentId).toList();
        var secondUserAppointments = getAppointmentsByPersonId(secondUser).stream().map(Appointment::getAppointmentId).toList();
        return firstUserAppointments.stream().filter(secondUserAppointments::contains).toList();
    }
}
