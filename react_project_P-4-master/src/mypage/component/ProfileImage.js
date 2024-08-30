import React from 'react';
import styled from 'styled-components';
import FileUpload from '../../commons/components/FileUpload';

const Wrapper = styled.div``;

const ProfileImage = ({ gid, profileImage, fileUploadCallback }) => {
  return (
    <Wrapper>
      <FileUpload
        ImageButton={profileImage}
        gid={gid}
        imageOnly={true}
        single={true}
        done={true}
        callback={fileUploadCallback}
      />
    </Wrapper>
  );
};

export default React.memo(ProfileImage);
