package com.cos.blog.test;

import java.util.List;
import java.util.function.Supplier;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cos.blog.model.RoleType;
import com.cos.blog.model.User;
import com.cos.blog.repository.UserRepository;

// html 파일이 아니라 data를 리턴해주는 controller = RestController
@RestController
public class DummyControllerTest {

	@Autowired // 의존성 주입 (DI)
	private UserRepository userRepository;
	
	// save 함수는 id를 전달하지 않으면 insert를 해주고
	// save 함수는 id를 전달하면 해당 id에 대한 데이터가 있으면 update를 해준다.
	// save 함수는 id를 전달하면 해당 id에 대한 데이터가 없으면 insert를 해준다.
	// email, password
	
	@DeleteMapping("/dummy/user/{id}")
	public String deleteUser(@PathVariable int id) {
		
		try {
			userRepository.deleteById(id) ;
		} catch (EmptyResultDataAccessException e) {
			return "삭제에 실패했습니다. 해당 id는 없습니다.";
		}
		
		return "삭제 완료 id: "+id;
		
	}
	
	@Transactional // 함수 종료시에 자동으로 commit이 된다.
	@PutMapping("/dummy/user/{id}")
	public User updateUser(@PathVariable int id, @RequestBody User requsetUser) {
		// json 데이터를 요청 => Java Obejct(MessageConverter의 Jackson 라이브러리) 로 변환해서 받는다.
		
		System.out.println("id: "+id);
		System.out.println("password: "+requsetUser.getPassword());
		System.out.println("email: "+requsetUser.getEmail());
		
		User user = userRepository.findById(id).orElseThrow(()->{
			return new IllegalArgumentException("수정에 실패했습니다.");
		});
		
		user.setPassword(requsetUser.getPassword());
		user.setEmail(requsetUser.getEmail());
			
		// userRepository.save(user);
		
		// 더티 체킹
		
		return user;
		
	}
	
	// http://localhost:8000/blog/dummy/user
	@GetMapping("/dummy/users")
	public List<User> list(){
		
		return userRepository.findAll();
		
	}
	
	// 한 페이징당 2건의 데이터를 리턴받아 볼 예정
	@GetMapping("/dummy/user")
	public Page<User> pageList(@PageableDefault(size = 2, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
		
		Page<User> pagingUsers = userRepository.findAll(pageable);
		
		List<User> users = pagingUsers.getContent();
		
		return pagingUsers;
		
	}
	
	// {id} 주소로 파라메터를 전달받을 수 있다.
	// http://localhost:8000/blog/dummy/user/3 
	@GetMapping("/dummy/user/{id}")
	public User detail(@PathVariable int id) {
		
		// user/4을 찾으면 내가 데이터베이스에서 못찾아오면 user가 null이 된다.
		// 그럼 return할 때 null이 리턴되고 프로그램에 문제가 생기지 않겠냐
		// Optional로 너의 User 객체를 감싸서 가져올테니 null인지 아닌지 판단해서
		// return하라는 뜻이다.
		User user = userRepository.findById(id).orElseThrow(new Supplier<IllegalArgumentException>() {
			@Override
			public IllegalArgumentException get() {
				return new IllegalArgumentException("해당 유저는 없습니다. id: "+id);
			}		
		// 요청: 웹브라우저
		// user 객체 = 자바 오브젝트
		// 변환 (웹브라우저가 이해할 수 있는 데이터) -> Json (Gson 라이브러리)
		// 스프링부트 = MessageConverter라는 애가 응답시에 자동 작동
		// 만약에 자바 오브젝트를 리턴하게 되면 MessageConverter가 
		// Jackson 라이브러리를 호출해서 user 오브젝트를 Json으로 변환해서
		// 브라우저에 던져줍니다.
		});
		
		return user;
	}
	
	// http://localhost:8000/blog/dummy/join (요청)
	// http의 body에 username, password, eamil 데이터를 가지고 (요청)
	
	@PostMapping("/dummy/join")
	public String join(User user) { // key=value (약속된 규칙)
		System.out.println("id: "+user.getId());
		System.out.println("username: "+user.getUsername());
		System.out.println("password: "+user.getPassword());
		System.out.println("email: "+user.getEmail());
		System.out.println("role: "+user.getRole());
		System.out.println("createDate: "+user.getCreateDate());
		
		user.setRole(RoleType.USER);
		
		userRepository.save(user);
		return "회원가입 완료";
	}
	
}
