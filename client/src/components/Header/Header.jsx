import React from 'react';
import { Link } from 'react-router-dom';
import styles from './Header.module.scss';

const Header = () => {
    return (
        <main className={styles.wrapper}>
            <section className={styles.searchSection}>
                <input type="text" placeholder="지역을 입력해 주세요." className={styles.searchInput} />
            </section>
            <nav className={styles.nav}>
                <Link to="/login" className={styles.link}>로그인</Link>
                <Link to="/join" className={styles.link}>회원가입</Link>
            </nav>
        </main>
    );
};

export default Header;