import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import styles from './Header.module.scss';

const Header = ({ onSearch, defaultValue = '', isLoading = false }) => {
    const [value, setValue] = useState(defaultValue);
    
    useEffect(() => {
        setValue(defaultValue);
    }, [defaultValue]);

    const handleSubmit = (e) => {
        e.preventDefault();
        onSearch(value);
    };

    return (
        <main className={styles.wrapper}>
            <section className={styles.searchSection}>
                <form onSubmit={handleSubmit}>
                    <input
                        type="text"
                        placeholder="지역을 입력해 주세요."
                        className={styles.searchInput}
                        value={value}
                        onChange={(e) => setValue(e.target.value)}
                        disabled={isLoading}
                    />
                </form>
            </section>
            <nav className={styles.nav}>
                <Link to="/login" className={styles.link}>로그인</Link>
                <Link to="/join" className={styles.link}>회원가입</Link>
            </nav>
        </main>
    );
};

export default Header;