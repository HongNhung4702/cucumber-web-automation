
@student @marks
Feature: Xem kết quả học tập của sinh viên
  Là sinh viên đã đăng nhập vào Cổng thông tin đào tạo
  Tôi muốn xem điểm theo học kỳ, chương trình đào tạo và học phần
  Để theo dõi kết quả học tập của tài khoản mình

  Background:
    Given sinh viên đã đăng nhập thành công vào hệ thống
    And họ tên và mã sinh viên trên giao diện khớp với tài khoản kiểm thử

  @TC_33 @smoke @data-verification
  Scenario: Xem điểm sinh viên theo năm học và học kỳ
    Given tài khoản sinh viên có dữ liệu điểm trong năm học "2025-2026" và học kỳ "Học kỳ 2"
    When sinh viên truy cập mục Xem điểm
    And sinh viên chọn chế độ Xem điểm sinh viên
    And sinh viên chọn chương trình đào tạo "Kỹ thuật phần mềm"
    And sinh viên chọn năm học "2025-2026"
    And sinh viên chọn học kỳ "Học kỳ 2"
    Then hệ thống hiển thị bảng điểm tương ứng với năm học và học kỳ đã chọn
    And tiêu đề bảng điểm hiển thị năm học "2025-2026" và học kỳ "HK02"
    And bảng điểm có các cột mã học phần, tên học phần, tín chỉ, điểm 10, điểm 4, điểm chữ và kết quả
    And các học phần hiển thị thuộc phạm vi năm học và học kỳ đã chọn
    And các học phần và kết quả điểm thuộc hồ sơ học tập của sinh viên đang đăng nhập
    And hệ thống hiển thị thông tin tổng tín chỉ, tín chỉ đạt và điểm trung bình học kỳ
    And hệ thống hiển thị thông tin tín chỉ tích lũy và điểm trung bình tích lũy
    And số tín chỉ đạt không vượt quá tổng tín chỉ của học kỳ
    And các giá trị điểm và tín chỉ nhất quán với dữ liệu học tập đã được xác minh của tài khoản

  @TC_34 @data-verification
  Scenario: Xem điểm theo chương trình đào tạo
    When sinh viên truy cập mục Xem điểm
    And sinh viên chọn chế độ Xem điểm theo chương trình đào tạo
    And sinh viên chọn chương trình đào tạo "Kỹ thuật phần mềm"
    Then hệ thống hiển thị bảng điểm theo chương trình đào tạo "Kỹ thuật phần mềm"
    And bảng điểm được phân nhóm theo học kỳ và nhóm học phần của chương trình đào tạo
    And các học phần hiển thị thuộc chương trình đào tạo đã chọn
    And bảng điểm hiển thị mã học phần, tên học phần, tín chỉ và kết quả điểm khi có dữ liệu
    And các học phần có điểm được đối chiếu với bảng Xem điểm sinh viên của cùng tài khoản
    And kết quả điểm của cùng học phần và lần học nhất quán giữa hai chế độ xem

  @TC_35 @details @data-verification
  Scenario: Xem điểm chi tiết của học phần Toán logic
    Given sinh viên đang ở trang Xem điểm
    And tài khoản có học phần "Toán logic" với mã học phần "1050074" và dữ liệu điểm chi tiết
    When sinh viên tìm học phần "Toán logic" trong bảng điểm
    And sinh viên nhấn biểu tượng Chi tiết tại dòng học phần
    Then hệ thống mở rộng khu vực điểm chi tiết của học phần được chọn
    And tiêu đề chi tiết hiển thị mã học phần "1050074" và tên học phần "Toán logic"
    And bảng chi tiết hiển thị các thành phần "Điểm Quá trình" và "Điểm Thi KTHP"
    And các điểm thành phần khớp với dữ liệu kiểm thử đã được xác minh
    And dữ liệu điểm chi tiết thuộc đúng học phần "Toán logic"
    And điểm tổng hợp của học phần không bị thay đổi khi mở chi tiết

  @TC_36 @filter @data-verification
  Scenario: Chuyển từ học kỳ có điểm sang học kỳ chưa có điểm và quay lại
    Given sinh viên đang ở trang Xem điểm
    And sinh viên đã chọn chế độ Xem điểm sinh viên
    And tài khoản có dữ liệu điểm trong năm học "2024-2025" và học kỳ "Học kỳ 2"
    And tài khoản có học phần nhưng chưa có điểm trong năm học "2026-2027" và học kỳ "Học kỳ 1"
    When sinh viên chọn chương trình đào tạo "Kỹ thuật phần mềm"
    And sinh viên chọn năm học "2024-2025"
    And sinh viên chọn học kỳ "Học kỳ 2"
    Then hệ thống hiển thị bảng điểm có dữ liệu của học kỳ đã chọn
    When sinh viên chọn năm học "2026-2027"
    And sinh viên chọn học kỳ "Học kỳ 1"
    Then bộ lọc hiển thị năm học "2026-2027" và học kỳ "Học kỳ 1"
    And hệ thống hiển thị danh sách học phần tương ứng với học kỳ chưa có điểm
    And các ô điểm của những học phần chưa có kết quả được để trống
    And không hiển thị nhầm điểm số của học kỳ "2024-2025" và "Học kỳ 2"
    And không giữ lại thông tin tổng kết học kỳ cũ trong phần tổng kết của học kỳ mới
    When sinh viên chọn lại năm học "2024-2025"
    And sinh viên chọn lại học kỳ "Học kỳ 2"
    Then hệ thống hiển thị lại bảng điểm của năm học "2024-2025" và học kỳ "Học kỳ 2"
    And danh sách học phần, các điểm số và thông tin tổng kết nhất quán với dữ liệu của lần hiển thị ban đầu
