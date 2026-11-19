
@student @exam @exam-term-switch
Feature: Kiểm thử thay đổi học kỳ khi xem lịch thi
  Là sinh viên đã đăng nhập
  Tôi muốn thay đổi học kỳ khi tra cứu lịch thi
  Để xem đúng lịch thi của học kỳ được chọn

  Background:
    Given sinh viên đã đăng nhập bằng tài khoản kiểm thử hợp lệ

  @TC_30 @filter @data-verification
  Scenario: Danh sách lịch thi cập nhật khi thay đổi học kỳ
    When sinh viên mở lịch thi học kỳ ban đầu TC_30
    Then lịch thi học kỳ ban đầu TC_30 khớp baseline

    When sinh viên đổi lịch thi sang học kỳ 2 TC_30
    Then lịch thi học kỳ 2 TC_30 khớp bộ lọc và baseline
