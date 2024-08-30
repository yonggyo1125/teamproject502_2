import React from 'react';
import { Tab } from 'react-bootstrap';
import { useTranslation } from 'react-i18next';
import styled from 'styled-components';
import Pagination from '../../commons/components/Pagination';
const NoDataText = styled.div`
  font-size: 16px;
  color: #818181;
`;

const TabReview = ({ items, pagination, onPageClick }) => {
  console.log(items);
  const { t } = useTranslation();
  return (
    <Tab.Pane eventKey="review">
      <div>
        {items && items.length > 0 ? (
          items.map(({ subject, content, poster, viewCount }) => (
            <>
              <h2>{subject}</h2>
              <p dangerouslySetInnerHTML={{ __html: content }}></p>
              <p>{poster}</p>
              <p>{viewCount}</p>
            </>
          ))
        ) : (
          <NoDataText>{t('리뷰_정보가_없습니다')}</NoDataText>
        )}
      </div>
      {pagination && (
        <Pagination pagination={pagination} onClick={onPageClick} />
      )}
    </Tab.Pane>
  );
};

export default React.memo(TabReview);
